package fortscale.web.services;


import fortscale.web.tasks.ControllerInvokedAdTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;

public interface AdTaskService {

    boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination);

    boolean cancelAllTasks(long terminationTimeout);

    Set<ControllerInvokedAdTask> getActiveTasks();
}
