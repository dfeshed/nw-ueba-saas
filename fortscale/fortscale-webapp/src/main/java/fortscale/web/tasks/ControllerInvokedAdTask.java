package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.services.ActiveDirectoryService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.ETL;
import static fortscale.web.tasks.ControllerInvokedAdTask.AdTaskType.FETCH;

public class ControllerInvokedAdTask implements Runnable {

    private static final Logger logger = Logger.getLogger(ControllerInvokedAdTask.class);

    private static final String TASK_RESULTS_PATH = "/tmp";
    private static final String DELIMITER = "=";
    private static final String KEY_SUCCESS = "success";
    private static final String COLLECTION_JAR_NAME = "${user.home.dir}/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar";
    private static final String THREAD_NAME = "deployment_wizard_fetch_and_etl";
    private static final String AD_JOB_GROUP = "AD";
    private static final String RESPONSE_DESTINATION = "/wizard/ad_fetch_etl_response";


    private final ApiActiveDirectoryController controller;
    private final AdObjectType dataSource;
    private AdTaskType currentAdTaskType;



    @Autowired
    private ActiveDirectoryService activeDirectoryService;


    public ControllerInvokedAdTask(ApiActiveDirectoryController apiActiveDirectoryController, AdObjectType dataSource) {
        this.controller = apiActiveDirectoryController;
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
        controller.setLastExecutionTime(currentAdTaskType, dataSource);

        if (!fetchResponse.isSuccess()) {
            logger.warn("Fetch phase failed so not executing ETL.");
            return;
        }

        currentAdTaskType = ETL;
        final AdTaskResponse etlResponse = executeAdTask(ETL, dataSource);
        controller.sendTemplateMessage(RESPONSE_DESTINATION, etlResponse);
        controller.setLastExecutionTime(currentAdTaskType, dataSource);
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
        logger.debug("Executing task {} for data source {}", adTaskType, dataSourceName);

        UUID resultsFileId = UUID.randomUUID();
        final String filePath = TASK_RESULTS_PATH + "/" + dataSourceName.toLowerCase() + "_" + adTaskType.toString().toLowerCase() + "_" + resultsFileId;

            /* run task */
        if (!runTask(dataSourceName, adTaskType, resultsFileId)) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
        }

            /* get task results from file */
        final Map<String, String> taskResults = getTaskResults(dataSourceName, adTaskType, filePath);
        if (taskResults == null) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
        }

            /* process results and understand if task finished successfully */
        final String success = taskResults.get(KEY_SUCCESS);
        if (success == null) {
            logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed", adTaskType, dataSourceName);
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSourceName);
        }

        /* get objects count for this data source from mongo */
        final long objectsCount = activeDirectoryService.getCount(dataSource);

        notifyTaskDone();
        return new AdTaskResponse(adTaskType, Boolean.valueOf(success), objectsCount, dataSourceName);
    }

    private Map<String, String> getTaskResults(Object dataSourceName, Object adTaskType, String filePath) {
        Map<String, String> taskResults = new HashMap<>();
        try {
            try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
                final List<String> lines = stream.collect(Collectors.toList());
                for (String line : lines) {
                    final String[] split = line.split(DELIMITER);
                    if (split.length != 2) {
                        logger.error("Invalid output for task {} for data source {}. Task Failed", adTaskType, dataSourceName);
                        return null;
                    }

                    taskResults.put(split[0], split[1]);
                }
            } catch (IOException e) {
                logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSourceName, e);
                return null;
            }
        } finally {
            try {
                Files.delete(Paths.get(filePath));
            } catch (IOException e) {
                logger.warn("Failed to delete results file {}.", filePath);
            }
        }

        return taskResults;
    }

    private boolean runTask(String dataSourceName, AdTaskType adTaskType, UUID resultsFileId) {
        Process process;
        try {
            final String jobName = dataSourceName + "_" + adTaskType.toString();
            final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", COLLECTION_JAR_NAME, jobName, AD_JOB_GROUP, "resultsFileId="+resultsFileId));
            process = new ProcessBuilder(arguments).start();
        } catch (IOException e) {
            logger.error("Execution of task {} for data source {} has failed.", adTaskType, dataSourceName, e);
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
            logger.error("Execution of task {} for data source {} has failed. Task has been interrupted", adTaskType, dataSourceName, e);
            return false;
        }

        if (status != 0) {
            logger.warn("Execution of task {} for step {} has finished with status {}. Execution failed", adTaskType, dataSourceName, status);
            return false;
        }

        logger.debug("Execution of task {} for step {} has finished with status {}", adTaskType, dataSourceName, status);
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

    public static class AdTaskResponse {
        private AdTaskType taskType;
        private boolean success;
        private long objectsCount;
        private String dataSource;

        public AdTaskResponse(AdTaskType taskType, boolean success, long objectsCount, String dataSource) {
            this.taskType = taskType;
            this.success = success;
            this.objectsCount = objectsCount;
            this.dataSource = dataSource;
        }

        public AdTaskType getTaskType() {
            return taskType;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getObjectsCount() {
            return objectsCount;
        }

        public String getDataSource() {
            return dataSource;
        }
    }

    public static class AdTaskStatus {
        public final AdTaskType runningMode; //null for not running
        public final AdObjectType datasource;
        public final Long lastExecutionTime;
        public final Long objectsCount;

        public AdTaskStatus(AdTaskType runningMode, AdObjectType datasource, Long lastExecutionTime, Long objectsCount) {
            this.runningMode = runningMode;
            this.datasource = datasource;
            this.lastExecutionTime = lastExecutionTime;
            this.objectsCount = objectsCount;
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


