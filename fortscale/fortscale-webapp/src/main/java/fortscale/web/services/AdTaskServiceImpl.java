package fortscale.web.services;


import fortscale.domain.ad.AdObject;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ad.AdTaskPersistencyService;
import fortscale.utils.logging.Logger;
import fortscale.web.tasks.CompoundControllerInvokedAdTask;
import fortscale.web.tasks.ControllerInvokedAdTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class AdTaskServiceImpl implements TaskService {

    private static final Logger logger = Logger.getLogger(AdTaskServiceImpl.class);

    private ActivityMonitoringExecutorService<ControllerInvokedAdTask> executorService;
    private ActiveDirectoryService activeDirectoryService;
    private AdTaskPersistencyService adTaskPersistencyService;
    private final Set<AdObject.AdObjectType> dataSources = new HashSet<>(Arrays.asList(AdObject.AdObjectType.values()));

    private AdTaskServiceImpl() {
        initExecutorService();
    }

    @Autowired
    public AdTaskServiceImpl(ActiveDirectoryService activeDirectoryService, AdTaskPersistencyService adTaskPersistencyService) {
        this.activeDirectoryService = activeDirectoryService;
        this.adTaskPersistencyService = adTaskPersistencyService;
        initExecutorService();
    }

    public boolean executeTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        initExecutorService();
        if (executorService.tryExecute()) {
            try {
                logger.info("Starting Active Directory fetch and ETL");
                final List<ControllerInvokedAdTask> adTasks = createAdTasks(simpMessagingTemplate, responseDestination);
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

    public Set<ControllerInvokedAdTask> getActiveTasks() {
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


    private List<ControllerInvokedAdTask> createAdTasks(SimpMessagingTemplate simpMessagingTemplate, String responseDestination) {
        final List<ControllerInvokedAdTask> tasks = new ArrayList<>();
        for (AdObject.AdObjectType dataSource : dataSources) {
            if (dataSource != AdObject.AdObjectType.USER_THUMBNAIL) { //user thumbnail job shouldn't run initially
                final ControllerInvokedAdTask currTask = new ControllerInvokedAdTask(executorService, simpMessagingTemplate, responseDestination, activeDirectoryService, adTaskPersistencyService, dataSource);
                if (currTask.getDataSource() == AdObject.AdObjectType.USER) { //user thumbnail job should run after user job
                    currTask.addFollowingTask(new CompoundControllerInvokedAdTask(executorService, simpMessagingTemplate, responseDestination, activeDirectoryService, adTaskPersistencyService, AdObject.AdObjectType.USER_THUMBNAIL));
                }
                tasks.add(currTask);
            }
        }

        return tasks;
    }

}
