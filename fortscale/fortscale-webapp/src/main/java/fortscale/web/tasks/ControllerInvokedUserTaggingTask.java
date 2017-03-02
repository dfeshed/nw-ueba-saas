package fortscale.web.tasks;

import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.services.users.tagging.UserTaggingTaskPersistencyServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.web.services.ActivityMonitoringExecutorService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.UUID;


public class ControllerInvokedUserTaggingTask extends BaseControllerInvokedTask implements Runnable {


    private static final Logger logger = Logger.getLogger(ControllerInvokedUserTaggingTask.class);

    protected static final String THREAD_NAME = "system_setup_user_tagging";

    private static final String USER_TAGGING_JOB_NAME = "User";
    private static final String USER_TAGGING_JOB_GROUP = "Tagging";
    public static final String USER_TAGGING_RESULT_ID = "result";

    private final String responseDestination;
    private final UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;

    protected final ActivityMonitoringExecutorService<ControllerInvokedUserTaggingTask> executorService;
    protected SimpMessagingTemplate simpMessagingTemplate;

    public ControllerInvokedUserTaggingTask(ActivityMonitoringExecutorService<ControllerInvokedUserTaggingTask> executorService,
                                            SimpMessagingTemplate simpMessagingTemplate, String responseDestination,
                                            UserTaggingTaskPersistenceService userTaggingTaskPersistenceService) {
        this.executorService = executorService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.responseDestination = responseDestination;
        this.userTaggingTaskPersistenceService = userTaggingTaskPersistenceService;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(THREAD_NAME);

        final boolean taskSucceeded = handleUserTaggingTask();
        if (!taskSucceeded) {
            logger.error("User tagging failed");
        }
    }

    private boolean handleUserTaggingTask() {
        try {
            final UserTaggingTaskResponse response = executeUserTaggingTask();
            simpMessagingTemplate.convertAndSend(responseDestination, response);
            userTaggingTaskPersistenceService.setLastExecutionTime(response.lastExecutionTime);
            if (!response.success) {
                logger.warn("{} has failed.", USER_TAGGING_JOB_NAME);
            }

            return response.success;
        } catch (Exception e) {
            logger.error("Failed to handle task {}.", USER_TAGGING_JOB_NAME, e);
            simpMessagingTemplate.convertAndSend(responseDestination, new UserTaggingTaskResponse(false, -1L, null));
            return false;
        }
    }


    /**
     * Runs a new process with the given arguments. This method is BLOCKING.
     *
     * @return an UserTaggingTaskResponse representing the results of the task
     */
    private UserTaggingTaskResponse executeUserTaggingTask() {
        userTaggingTaskPersistenceService.setExecutionStartTime(System.currentTimeMillis());
        notifyTaskStart();

        final String resultsKey = userTaggingTaskPersistenceService.createResultKey(USER_TAGGING_RESULT_ID);

        /* run task */
        logger.info("Running user tagging task {}", USER_TAGGING_JOB_NAME);
        if (!runCollectionJob(USER_TAGGING_JOB_NAME, USER_TAGGING_RESULT_ID, USER_TAGGING_JOB_GROUP)) {
            notifyTaskDone();
            return new UserTaggingTaskResponse(false, -1L, null);
        }


        /* get task results from file */
        logger.debug("Getting results for task {} with results key {}", USER_TAGGING_JOB_NAME, resultsKey);
        final UserTaggingTaskPersistencyServiceImpl.UserTaggingResult taskResults = userTaggingTaskPersistenceService.getTaskResults(resultsKey);
        if (taskResults == null) {
            notifyTaskDone();
            return new UserTaggingTaskResponse(false, -1L, null);
        }

        /* process results and understand if task finished successfully */
        final Boolean success = taskResults.isSuccess();
        if (success == null) {
            logger.error("Invalid output for task {} . success status is missing. Task Failed", USER_TAGGING_JOB_NAME);
            notifyTaskDone();
            return new UserTaggingTaskResponse(false, -1L, null);
        }

        notifyTaskDone();
        final long lastExecutionTime = System.currentTimeMillis();
        return new UserTaggingTaskResponse(Boolean.valueOf(success), lastExecutionTime, taskResults.getUsersAffected());
    }

    @Override
    protected ActivityMonitoringExecutorService getActivityMonitoringExecutorService() {
        return executorService;
    }

    /**
     * This class represents an UserTagging response to the controller that executed it containing various information the controller needs to return the UI
     */
    public static class UserTaggingTaskResponse {
        private boolean success;
        private Long lastExecutionTime;
        private Map<String, Long> taggingResult;

        public UserTaggingTaskResponse(boolean success, Long lastExecutionTime, Map<String, Long> taggingResult) {
            this.success = success;
            this.lastExecutionTime = lastExecutionTime;
            this.taggingResult = taggingResult;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Long getLastExecutionTime() {
            return lastExecutionTime;
        }

        public void setLastExecutionTime(Long lastExecutionTime) {
            this.lastExecutionTime = lastExecutionTime;
        }

        public Map<String, Long> getTaggingResult() {
            return taggingResult;
        }

        public void setTaggingResult(Map<String, Long> taggingResult) {
            this.taggingResult = taggingResult;
        }
    }
}



