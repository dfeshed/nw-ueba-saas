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

/**
 * Created by alexp on 16/02/2017.
 */
@Service
public class UserTaggingTaskServiceImpl extends TaskService {
    private static final Logger logger = Logger.getLogger(UserTaggingTaskServiceImpl.class);

    private UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;
    private final Set<AdObject.AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObject.AdObjectType.values()));

    private UserTaggingTaskServiceImpl() {
        initExecutorService(1);
    }

    @Autowired
    public UserTaggingTaskServiceImpl(UserTaggingTaskPersistenceService userTaggingTaskPersistenceService) {
        this.userTaggingTaskPersistenceService = userTaggingTaskPersistenceService;
        initExecutorService(1);
    }

    @Override
    public boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        initExecutorService(1);
        if (executorService.tryExecute()) {
            try {
                logger.info("Starting user tagging");
                final List<ControllerInvokedUserTaggingTask> adTasks = createTaggingTask(simpMessagingTemplate, responseDestination);
                executorService.executeTasks(adTasks);
                return true;
            } finally {
                executorService.markEndExecution();
            }
        } else {
            return false;
        }
    }

    private List<ControllerInvokedUserTaggingTask> createTaggingTask(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        final List<ControllerInvokedUserTaggingTask> tasks = new ArrayList<>();
        tasks.add(new ControllerInvokedUserTaggingTask(executorService, simpMessagingTemplate, responseDestination, userTaggingTaskPersistenceService));
        return tasks;
    }
}
