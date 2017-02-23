package fortscale.web.services;

import fortscale.domain.ad.AdObject;
import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.utils.logging.Logger;
import fortscale.web.tasks.ControllerInvokedUserTaggingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexp on 16/02/2017.
 */
@Service
public class UserTaggingTaskServiceImpl implements UserTaggingTaskService {
    private static final Logger logger = Logger.getLogger(UserTaggingTaskServiceImpl.class);

    private ActivityMonitoringExecutorService<ControllerInvokedUserTaggingTask> executorService;
    private UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;
    private final Set<AdObject.AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObject.AdObjectType.values()));

    private UserTaggingTaskServiceImpl() {
        initExecutorService();
    }

    @Autowired
    public UserTaggingTaskServiceImpl(UserTaggingTaskPersistenceService userTaggingTaskPersistenceService) {
        this.userTaggingTaskPersistenceService = userTaggingTaskPersistenceService;
        initExecutorService();
    }

    @Override
    public boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        initExecutorService();
        if (executorService.tryExecute()) {
            try {
                logger.info("Starting user tagging");
                final List<ControllerInvokedUserTaggingTask> adTasks = createTaggingTask(simpMessagingTemplate, responseDestination);
                executorService.executeTasks(adTasks);
                return true;
            } finally {
                executorService.markEndExecution();
            }
        }
        else {
            return false;
        }
    }

    public boolean cancelAllTasks(long terminationTimeout) {
        if (executorService.isHasActiveTasks()) {
            logger.info("Attempting to kill all running threads {}", executorService.getActiveTasks());
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(terminationTimeout, TimeUnit.SECONDS);
                return true;
            } catch (InterruptedException e) {
                final String msg = "Failed to await termination of running threads.";
                logger.error(msg);
                return false;
            }
        } else {
            final String msg = "Attempted to cancel threads was made but there are no running tasks.";
            logger.warn(msg);
            return false;
        }
    }

    public Set<ControllerInvokedUserTaggingTask> getActiveTasks() {
        return executorService.getActiveTasks();
    }

    private void initExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            return; // use the already working executor service
        }
        else {
            executorService = new ActivityMonitoringExecutorServiceImpl<>(
                    Executors.newFixedThreadPool(dataSources.size(), runnable -> {
                        Thread thread = new Thread(runnable);
                        thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
                        return thread;
                    }),
                    dataSources.size());
        }
    }

    private List<ControllerInvokedUserTaggingTask> createTaggingTask(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        final List<ControllerInvokedUserTaggingTask> tasks = new ArrayList<>();
        tasks.add(new ControllerInvokedUserTaggingTask(executorService, simpMessagingTemplate, responseDestination, userTaggingTaskPersistenceService));
        return tasks;
    }
}
