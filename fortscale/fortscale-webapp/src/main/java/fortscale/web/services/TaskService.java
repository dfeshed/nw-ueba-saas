package fortscale.web.services;


import fortscale.utils.logging.Logger;
import fortscale.web.tasks.BasicControllerInvokedTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class TaskService<T extends BasicControllerInvokedTask> {
    private static final Logger logger = Logger.getLogger(TaskService.class);

    public abstract boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination);
    abstract void initExecutorService();
    abstract  ActivityMonitoringExecutorService<T> getExecuterService();

    public boolean cancelAllTasks(long terminationTimeout) {
        if (getExecuterService().isHasActiveTasks()) {
            logger.info("Attempting to kill all running threads {}", getExecuterService().getActiveTasks());
            getExecuterService().shutdownNow();
            try {
                getExecuterService().awaitTermination(terminationTimeout, TimeUnit.SECONDS);
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

    public Set<T> getActiveTasks() {
        return getExecuterService().getActiveTasks();
    }
}
