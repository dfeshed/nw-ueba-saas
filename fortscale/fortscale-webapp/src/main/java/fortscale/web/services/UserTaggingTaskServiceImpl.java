package fortscale.web.services;

import fortscale.domain.ad.AdObject;
import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.utils.logging.Logger;
import fortscale.web.tasks.ControllerInvokedUserTaggingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by alexp on 16/02/2017.
 */
@Service(value = "UserTaggingTaskServiceImpl")
public class UserTaggingTaskServiceImpl extends TaskService {
    private static final Logger logger = Logger.getLogger(UserTaggingTaskServiceImpl.class);

    private UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;
    private final Set<AdObject.AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObject.AdObjectType.values()));
    private String userTaggingFilePath;

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
                final List<ControllerInvokedUserTaggingTask> taggingTask = createTaggingTask(simpMessagingTemplate, responseDestination);
                executorService.executeTasks(taggingTask);
                return true;
            } finally {
                userTaggingFilePath = null;
                executorService.markEndExecution();
            }
        } else {
            return false;
        }
    }

    private List<ControllerInvokedUserTaggingTask> createTaggingTask(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        final List<ControllerInvokedUserTaggingTask> tasks = new ArrayList<>();
        tasks.add(new ControllerInvokedUserTaggingTask(executorService, simpMessagingTemplate, responseDestination, userTaggingTaskPersistenceService, userTaggingFilePath));
        return tasks;
    }

    public void setUserTaggingFilePath(String userTaggingFilePath) {
        this.userTaggingFilePath = userTaggingFilePath;
    }
}
