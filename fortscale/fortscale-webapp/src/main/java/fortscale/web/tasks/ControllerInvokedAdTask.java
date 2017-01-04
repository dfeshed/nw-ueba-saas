package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.ETL;
import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.FETCH;

public class ControllerInvokedAdTask implements Runnable {


    private static final Logger logger = Logger.getLogger(ControllerInvokedAdTask.class);

    protected static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";

    private static final String RESULTS_DELIMITER = "=";
    private static final String RESULTS_KEY_SUCCESS = "success";
    private static final String AD_JOB_GROUP = "AD";
    private static final String RESPONSE_DESTINATION = "/wizard/ad_fetch_etl_response";


    protected final ApiActiveDirectoryController controller;
    private ActiveDirectoryService activeDirectoryService;
    private final ApplicationConfigurationService applicationConfigurationService;
    protected final AdObjectType dataSource;
    protected AdTaskType currentAdTaskType;
    protected List<ControllerInvokedAdTask> followingTasks = new ArrayList<>();

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

    public List<ControllerInvokedAdTask> getFollowingTasks() {
        return Collections.unmodifiableList(followingTasks); //defensive copy
    }

    public void addFollowingTask(ControllerInvokedAdTask taskToAdd) {
        followingTasks.add(taskToAdd);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);

        currentAdTaskType = FETCH;
        final boolean fetchTaskSucceeded = handleAdTask(currentAdTaskType);
        if (!fetchTaskSucceeded) {
            logger.error("ETL phase for data source {} has been cancelled since Fetch phase has failed.", dataSource);
            return;
        }

        currentAdTaskType = ETL;
        final boolean etlTaskSucceeded = handleAdTask(currentAdTaskType);
        logger.info("Finished executing Fetch and ETL for datasource {}", dataSource);

        if (!followingTasks.isEmpty()) {
            if (!etlTaskSucceeded) {
                logger.warn("There are following task {}, but task didn't succeed so they will not be executed");
            }
            else {
                logger.info("Running task {}'s following tasks {}", this, followingTasks);
                controller.executeTasks(followingTasks);
            }
        }
    }

    protected boolean handleAdTask(AdTaskType adTaskType) {
        final AdTaskResponse response = executeAdTask(adTaskType, dataSource);
        controller.sendTemplateMessage(RESPONSE_DESTINATION, response);
        controller.setLastExecutionTime(currentAdTaskType, dataSource, response.lastExecutionTime);
        if (!response.success) {
            logger.warn("{} phase for data source {} has failed.", adTaskType, dataSource);
        }

        return response.success;
    }

    private void notifyTaskStart() {
        if (!controller.addActiveTask(this)) {
            logger.warn("Tried to add task {} but the task already exists.", this);
        }
        else {
            logger.debug("added running task {} to active tasks", this);
        }
    }


    private void notifyTaskDone() {
        if (!controller.removeActiveTask(this)) {
            logger.warn("Tried to remove task {} but task doesn't exist.", this);
        }
        else {
            logger.debug("Removed running task {} from active tasks", this);
        }
    }


    /**
     * Runs a new process with the given arguments. This method is BLOCKING.
     * @param adTaskType the type of task to run (fetch/etl)
     * @param dataSource the data source (user/groups/etc..)
     * @return an AdTaskResponse representing the results of the task
     */
    private AdTaskResponse executeAdTask(AdTaskType adTaskType, AdObjectType dataSource) {
        controller.setExecutionStartTime(adTaskType, dataSource, System.currentTimeMillis());
        notifyTaskStart();
        final String dataSourceName = dataSource.toString();

        UUID resultsId = UUID.randomUUID();
        final String resultsKey = dataSourceName.toLowerCase() + "_" + adTaskType.toString().toLowerCase() + "." + resultsId;

        /* run task */
        final String jobName = dataSourceName + "_" + adTaskType.toString();
        logger.info("Running AD task {} with ID {}", jobName, resultsId);
        if (!runCollectionJob(jobName, resultsId)) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }


        /* get task results from file */
        logger.info("Getting results for task {} with results key {}", jobName, resultsKey);
        final Map<String, String> taskResults = getTaskResults(resultsKey);
        if (taskResults == null) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }

        /* process results and understand if task finished successfully */
        final String success = taskResults.get(RESULTS_KEY_SUCCESS);
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
            taskResults.put(RESULTS_KEY_SUCCESS, Boolean.FALSE.toString());
            return taskResults;
        }

        final String taskExecutionResult = queryResult.getValue();
        final String[] split = taskExecutionResult.split(RESULTS_DELIMITER);
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
            final String scriptPath = controller.COLLECTION_TARGET_DIR + "/resources/scripts/runAdTask.sh"; // this scripts runs the fetch/etl
            final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("/usr/bin/sudo", "-u", "cloudera", scriptPath, jobName, AD_JOB_GROUP, "resultsId="+resultsId));
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments).redirectErrorStream(true);
            processBuilder.directory(new File(controller.COLLECTION_TARGET_DIR));
            processBuilder.redirectErrorStream(true);
            logger.debug("Starting process with arguments {}", arguments);
            process = processBuilder.start();
        } catch (IOException e) {
            logger.error("Execution of task {} has failed.", jobName, e);
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
            try {
                String processOutput = IOUtils.toString(process.getInputStream());
                final int length = processOutput.length();
                if (length > 1000) {
                    processOutput = processOutput.substring(length - 1000, length); // getting last 1000 chars to not overload the log file
                }
                logger.error("Error stream for job {} = \n{}", jobName, processOutput);
            } catch (IOException e) {
                logger.warn("Failed to get error stream from process for job {}", jobName);
            }
            logger.error("Execution of task {} has finished with status {}. Execution failed", jobName, status);
            return false;
        }

        logger.info("Execution of task {} has finished with status {}", jobName, status);
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
                ", currentAdTaskType=" + currentAdTaskType +
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
        private final Long executionStartTime;
        private final Long objectsCount;

        public AdTaskStatus(AdTaskType runningMode, AdObjectType datasource, Long lastExecutionFinishTime, Long executionStartTime, Long objectsCount) {
            this.runningMode = runningMode;
            this.datasource = datasource;
            this.lastExecutionFinishTime = lastExecutionFinishTime;
            this.executionStartTime = executionStartTime;
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

        public Long getExecutionStartTime() {
            return executionStartTime;
        }

        public Long getObjectsCount() {
            return objectsCount;
        }
    }



    public enum AdTaskType {
        FETCH("Fetch"), ETL("ETL"), FETCH_ETL(FETCH.getType() + "_" + ETL.getType());

        private final String type;

        AdTaskType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}


