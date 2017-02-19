package fortscale.web.services;


import fortscale.web.tasks.ControllerInvokedUserTaggingTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Set;

public interface UserTaggingTaskService {

    boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination);

    boolean cancelAllTasks(long terminationTimeout);

    Set<ControllerInvokedUserTaggingTask> getActiveTasks();
}
