package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.domain.ad.AdTaskType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ad.AdTaskPersistencyService;
import fortscale.utils.logging.Logger;
import fortscale.web.services.ActivityMonitoringExecutorService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import static fortscale.domain.ad.AdTaskType.*;

public class ControllerInvokedAdTask extends BaseControllerInvokedTask implements Runnable {


    private static final Logger logger = Logger.getLogger(ControllerInvokedAdTask.class);

    protected static final String THREAD_NAME = "system_setup";

    private static final String AD_JOB_GROUP = "AD";

    private final String responseDestination;
    private final ActiveDirectoryService activeDirectoryService;
    private final AdTaskPersistencyService adTaskPersistencyService;

    protected final ActivityMonitoringExecutorService<ControllerInvokedAdTask> executorService;
    protected SimpMessagingTemplate simpMessagingTemplate;
    protected final AdObjectType dataSource;
    protected AdTaskType currentAdTaskType;
    protected List<ControllerInvokedAdTask> followingTasks = new ArrayList<>();

    public ControllerInvokedAdTask(ActivityMonitoringExecutorService<ControllerInvokedAdTask> executorService,
                                   SimpMessagingTemplate simpMessagingTemplate, String responseDestination,
                                   ActiveDirectoryService activeDirectoryService,
                                   AdTaskPersistencyService adTaskPersistencyService, AdObjectType dataSource) {
        this.executorService = executorService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.responseDestination = responseDestination;
        this.activeDirectoryService = activeDirectoryService;
        this.adTaskPersistencyService = adTaskPersistencyService;
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
        currentAdTaskType = FETCH;
        Thread.currentThread().setName(THREAD_NAME + "_" + currentAdTaskType + "_" + dataSource);

        final boolean fetchTaskSucceeded = handleAdTask(currentAdTaskType);
        if (!fetchTaskSucceeded) {
            logger.error("ETL phase for data source {} has been cancelled since Fetch phase has failed.", dataSource);
            return;
        }

        currentAdTaskType = ETL;
        Thread.currentThread().setName(THREAD_NAME + "_" + currentAdTaskType + "_" + dataSource);
        final boolean etlTaskSucceeded = handleAdTask(currentAdTaskType);
        logger.info("Finished executing Fetch and ETL for datasource {}", dataSource);

        if (!followingTasks.isEmpty()) {
            if (!etlTaskSucceeded) {
                logger.warn("There are following task {}, but task didn't succeed so they will not be executed");
            }
            else {
                logger.info("Running task {}'s following tasks {}", this, followingTasks);
                executorService.executeTasks(followingTasks);
            }
        }
    }

    protected boolean handleAdTask(AdTaskType adTaskType) {
        try {
            final AdTaskResponse response = executeAdTask(adTaskType, dataSource);
            simpMessagingTemplate.convertAndSend(responseDestination, response);
            adTaskPersistencyService.setLastExecutionTime(currentAdTaskType, dataSource, response.lastExecutionTime);
            if (!response.success) {
                logger.warn("{} phase for data source {} has failed.", adTaskType, dataSource);
            }

            return response.success;
        } catch (Exception e) {
            logger.error("Failed to handle task {} for data source {}.", adTaskType, dataSource, e);
            simpMessagingTemplate.convertAndSend(responseDestination, new AdTaskResponse(adTaskType, false, -1, dataSource, -1L ));
            return false;
        }
    }

    /**
     * Runs a new process with the given arguments. This method is BLOCKING.
     * @param adTaskType the type of task to run (fetch/etl)
     * @param dataSource the data source (user/groups/etc..)
     * @return an AdTaskResponse representing the results of the task
     */
    private AdTaskResponse executeAdTask(AdTaskType adTaskType, AdObjectType dataSource) {
        adTaskPersistencyService.setExecutionStartTime(adTaskType, dataSource, System.currentTimeMillis());
        notifyTaskStart();
        final String dataSourceName = dataSource.toString();

        UUID resultsId = UUID.randomUUID();
        final String resultsKey = adTaskPersistencyService.createResultKey(dataSource, adTaskType, resultsId);

        /* run task */
        final String jobName = dataSourceName + "_" + adTaskType.toString();
        logger.info("Running AD task {} with ID {}", jobName, resultsId);
        final Map<String, String> additionalParams = Collections.singletonMap("resultsId", resultsId.toString());
        if (!runCollectionJob(jobName, AD_JOB_GROUP, additionalParams)) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }


        /* get task results from file */
        logger.debug("Getting results for task {} with results key {}", jobName, resultsKey);
        final Map<String, String> taskResults = adTaskPersistencyService.getTaskResults(resultsKey);
        if (taskResults == null) {
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }

        /* process results and understand if task finished successfully */
        final String success = taskResults.get(AdTaskPersistencyService.RESULTS_KEY_SUCCESS);
        if (success == null) {
            logger.error("Invalid output for task {} for data source {}. success status is missing. Task Failed",
                    adTaskType, dataSourceName);
            notifyTaskDone();
            return new AdTaskResponse(adTaskType, false, -1, dataSource, -1L);
        }

        /* get objects count for this data source from mongo (if it's a Fetch job we don't care about the count)*/
        final long objectsCount = (adTaskType==ETL || adTaskType==FETCH_ETL)? activeDirectoryService.getLastRunCount(dataSource) : -1;


        notifyTaskDone();
        final long lastExecutionTime = System.currentTimeMillis();
        return new AdTaskResponse(adTaskType, Boolean.valueOf(success), objectsCount, dataSource, lastExecutionTime);
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
        return getClass() + "{" +
                "dataSource=" + dataSource +
                ", currentAdTaskType=" + currentAdTaskType +
                '}';
    }

    @Override
    protected ActivityMonitoringExecutorService getActivityMonitoringExecutorService() {
        return executorService;
    }


    /**
     * This class represents an ADTask response to the controller that executed it containing various information the
     * controller needs to return the UI
     */
    public static class AdTaskResponse {
        private AdTaskType taskType;
        private boolean success;
        private long objectsCount;
        private AdObjectType dataSource;
        private Long lastExecutionTime;

        public AdTaskResponse(AdTaskType taskType, boolean success, long objectsCount, AdObjectType dataSource,
                              Long lastExecutionTime) {
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

        public AdTaskStatus(AdTaskType runningMode, AdObjectType datasource, Long lastExecutionFinishTime,
                            Long executionStartTime, Long objectsCount) {
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

}


