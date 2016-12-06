package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.ETL;
import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.FETCH;

public class ControllerInvokedAdTask implements Runnable {


    private static final Logger logger = Logger.getLogger(ControllerInvokedAdTask.class);

    private static final String DELIMITER = "=";
    private static final String KEY_SUCCESS = "success";
    private static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";
    private static final String AD_JOB_GROUP = "AD";
    private static final String RESPONSE_DESTINATION = "/wizard/ad_fetch_etl_response";


    private final ApiActiveDirectoryController controller;
    private ActiveDirectoryService activeDirectoryService;
    private final ApplicationConfigurationService applicationConfigurationService;
    private final AdObjectType dataSource;
    private AdTaskType currentAdTaskType;

    public ControllerInvokedAdTask(ApiActiveDirectoryController controller, ActiveDirectoryService activeDirectoryService, ApplicationConfigurationService applicationConfigurationService, AdObjectType dataSource) {
        this.controller = controller;
        this.activeDirectoryService = activeDirectoryService;
        this.applicationConfigurationService = applicationConfigurationService;
        this.dataSource = dataSource;
    }

    public AdObjectType getDataSource() {
        return dataSource;
    }

    public AdTaskType getCurrentAdTaskType() {
        return currentAdTaskType;
    }



    @Override
    public void run() {
        currentAdTaskType = FETCH;
        notifyTaskStart();
        Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);

        final AdTaskResponse fetchResponse = executeAdTask(FETCH, dataSource);
        controller.sendTemplateMessage(RESPONSE_DESTINATION, fetchResponse);

        if (!fetchResponse.success) {
            logger.warn("Fetch phase failed so not executing ETL.");
            return;
        }

        currentAdTaskType = ETL;
        final AdTaskResponse etlResponse = executeAdTask(ETL, dataSource);
        controller.sendTemplateMessage(RESPONSE_DESTINATION, etlResponse);
        controller.setLastExecutionTime(currentAdTaskType, dataSource, fetchResponse.lastExecutionTime);
    }

    private void notifyTaskStart() {
        if (!controller.addRunningTask(this)) {
            logger.warn("Tried to add task but the task already exists. This may occur due to concurrency issues.");
        }
    }


    private void notifyTaskDone() {
        if (!controller.removeRunningTask(this)) {
            logger.warn("Tried to remove task but task doesn't exist. This may occur due to concurrency issues.");
        }
    }




    /**
     * Runs a new process with the given arguments. This method is BLOCKING.
     * @param adTaskType the type of task to run (fetch/etl)
     * @param dataSource the data source (user/groups/etc..)
     * @return an AdTaskResponse representing the results of the task
     */
    private AdTaskResponse executeAdTask(AdTaskType adTaskType, AdObjectType dataSource) {
        final String dataSourceName = dataSource.toString();

        UUID resultsId = UUID.randomUUID();
        final String resultsKey = dataSourceName.toLowerCase() + "_" + adTaskType.toString().toLowerCase() + "." + resultsId;

        /* run task */
        final String jobName = dataSourceName + "_" + adTaskType.toString();
        logger.debug("Running AD task {} with ID {}", jobName, resultsId);
        if (!runCollectionJob(jobName, resultsId)) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }


        /* get task results from file */
        logger.debug("Getting results for task {} with results key {}", jobName, resultsKey);
        final Map<String, String> taskResults = getTaskResults(resultsKey);
        if (taskResults == null) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }

        /* process results and understand if task finished successfully */
        final String success = taskResults.get(KEY_SUCCESS);
        if (success == null) {
            logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed", adTaskType, dataSourceName);
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }

        /* get objects count for this data source from mongo (if it's a Fetch job we don't care about the count)*/
        final long objectsCount = adTaskType==ETL? activeDirectoryService.getCount(dataSource) : -1;


        notifyTaskDone();
        final long lastExecutionTime = System.currentTimeMillis();
        return new AdTaskResponse(adTaskType, Boolean.valueOf(success), objectsCount, dataSource, lastExecutionTime);
    }

    private Map<String, String> getTaskResults(String resultsKey) {
        Map<String, String> taskResults = new HashMap<>();
        ApplicationConfiguration queryResult = applicationConfigurationService.getApplicationConfiguration(resultsKey);
        if (queryResult == null) {
            logger.error("No result found for result key {}. Task failed", resultsKey);
            taskResults.put(KEY_SUCCESS, Boolean.FALSE.toString());
            return taskResults;
        }

        final String taskExecutionResult = queryResult.getValue();
        final String[] split = taskExecutionResult.split(DELIMITER);
        final String key = split[0];
        final String value = split[1];
        taskResults.put(key, value);
        if (applicationConfigurationService.delete(resultsKey) == 0) {
            logger.warn("Failed to delete query result with key {}.", resultsKey);
        }

        return taskResults;
    }

    /**
     * This method creates a new PROCESS and runs the given collection job
     * @param jobName the collection job name for the task (example User_Fetch AD)
     * @param resultsId the random id that will be given to this job execution results in application configuration
     * @return true if the execution finished successfully, false otherwise
     */
    private boolean runCollectionJob(String jobName, UUID resultsId) {
        Process process;
        try {
            final String collectionJarPath = ApiActiveDirectoryController.COLLECTION_TARGET_DIR + "/" + ApiActiveDirectoryController.COLLECTION_JAR_NAME;
            if(!Files.exists(Paths.get(collectionJarPath))) {
                logger.error("Execution of task {} has failed. Collection jar file doesn't exist in {}", jobName, collectionJarPath);
                return false;
            }
            final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", collectionJarPath, jobName, AD_JOB_GROUP, "resultsId="+resultsId));
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments);
            processBuilder.directory(new File(ApiActiveDirectoryController.COLLECTION_TARGET_DIR));
            process = processBuilder.start();
        } catch (IOException e) {
            logger.error("Execution of task {}  has failed.", jobName, e);
            return false;
        }
        int status;
        try {
            status = process.waitFor();
        } catch (InterruptedException e) {
            if (process.isAlive()) {
                logger.error("Killing the process forcibly");
                process.destroyForcibly();
            }
            logger.error("Execution of task {} has failed. Task has been interrupted", jobName, e);
            return false;
        }

        if (status != 0) {
            logger.warn("Execution of task {}  has finished with status {}. Execution failed", jobName, status);
            return false;
        }

        logger.debug("Execution of task {} has finished with status {}", jobName, status);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerInvokedAdTask that = (ControllerInvokedAdTask) o;
        return dataSource == that.dataSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSource);
    }

    @Override
    public String toString() {
        return "ControllerInvokedAdTask{" +
                "dataSource=" + dataSource +
                '}';
    }

    /**
     * This class represents an ADTask response to the controller that executed it containing various information the controller needs to return the UI
     */
    public static class AdTaskResponse {
        private AdTaskType taskType;
        private boolean success;
        private long objectsCount;
        private AdObjectType dataSource;
        private Long lastExecutionTime;

        public AdTaskResponse(AdTaskType taskType, boolean success, long objectsCount, AdObjectType dataSource, Long lastExecutionTime) {
            this.taskType = taskType;
            this.success = success;
            this.objectsCount = objectsCount;
            this.dataSource = dataSource;
            this.lastExecutionTime = lastExecutionTime;
        }

        public AdTaskType getTaskType() {
            return taskType;
        }

        public void setTaskType(AdTaskType taskType) {
            this.taskType = taskType;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public long getObjectsCount() {
            return objectsCount;
        }

        public void setObjectsCount(long objectsCount) {
            this.objectsCount = objectsCount;
        }

        public AdObjectType getDataSource() {
            return dataSource;
        }

        public void setDataSource(AdObjectType dataSource) {
            this.dataSource = dataSource;
        }

        public Long getLastExecutionTime() {
            return lastExecutionTime;
        }

        public void setLastExecutionTime(Long lastExecutionTime) {
            this.lastExecutionTime = lastExecutionTime;
        }
    }

    public static class AdTaskStatus {
        private final AdTaskType runningMode; //null for not running
        private final AdObjectType datasource;
        private final Long lastExecutionFinishTime;
        private final Long objectsCount;

        public AdTaskStatus(AdTaskType runningMode, AdObjectType datasource, Long lastExecutionFinishTime, Long objectsCount) {
            this.runningMode = runningMode;
            this.datasource = datasource;
            this.lastExecutionFinishTime = lastExecutionFinishTime;
            this.objectsCount = objectsCount;
        }

        public AdTaskType getRunningMode() {
            return runningMode;
        }

        public AdObjectType getDatasource() {
            return datasource;
        }

        public Long getLastExecutionFinishTime() {
            return lastExecutionFinishTime;
        }

        public Long getObjectsCount() {
            return objectsCount;
        }
    }



    public enum AdTaskType {
        FETCH("Fetch"), ETL("ETL");

        private final String type;

        AdTaskType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }



}


