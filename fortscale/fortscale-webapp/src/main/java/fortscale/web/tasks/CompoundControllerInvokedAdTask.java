package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;


public class CompoundControllerInvokedAdTask extends ControllerInvokedAdTask {

    private static final Logger logger = Logger.getLogger(CompoundControllerInvokedAdTask.class);

    public CompoundControllerInvokedAdTask(ApiActiveDirectoryController controller, ActiveDirectoryService activeDirectoryService, ApplicationConfigurationService applicationConfigurationService, AdObjectType dataSource) {
        super(controller, activeDirectoryService, applicationConfigurationService, dataSource);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(THREAD_NAME + "_" + dataSource);

        currentAdTaskType = AdTaskType.FETCH_ETL;
        handleAdTask(currentAdTaskType);
        logger.info("Finished executing Fetch and ETL for datasource {}", dataSource);

        if (!followingTasks.isEmpty()) {
            logger.info("Running task {}'s following tasks {}", this, followingTasks);
            controller.executeTasks(followingTasks);
        }
    }

}
