package fortscale.web.services;


import fortscale.utils.logging.Logger;
import fortscale.web.tasks.BaseControllerInvokedTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class TaskService<T extends BaseControllerInvokedTask> {
    private static final Logger logger = Logger.getLogger(TaskService.class);
    protected ActivityMonitoringExecutorServiceImpl executorService;

    public abstract boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination);

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

    protected void initExecutorService(int threadPoolSize) {
        if (executorService != null && !executorService.isShutdown()) {
            return; // use the already working executor service
        } else {
            executorService = new ActivityMonitoringExecutorServiceImpl<>(
                    Executors.newFixedThreadPool(threadPoolSize, runnable -> {
                        Thread thread = new Thread(runnable);
                        thread.setUncaughtExceptionHandler((exceptionThrowingThread, e) -> logger.error("Thread {} threw an uncaught exception", exceptionThrowingThread.getName(), e));
                        return thread;
                    }),
                    threadPoolSize);
        }
    }

    public Set<T> getActiveTasks() {
        return executorService.getActiveTasks();
    }
}
