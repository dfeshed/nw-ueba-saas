package fortscale.web.services;


import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;

public interface TaskService<T extends ActivityMonitoringExecutorService> {

    boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination);

    boolean cancelAllTasks(long terminationTimeout);

    Set<T> getActiveTasks();
}
