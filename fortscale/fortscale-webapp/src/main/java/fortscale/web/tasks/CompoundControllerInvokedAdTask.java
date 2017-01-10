package fortscale.web.tasks;

import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;

/**
 * this class represents ControllerInvokedAdTask that do compound work like Fetch+ETL (example task is user_thumbnail_fetch_etl)
 */
public class CompoundControllerInvokedAdTask extends ControllerInvokedAdTask {

    private static final Logger logger = Logger.getLogger(CompoundControllerInvokedAdTask.class);

    public CompoundControllerInvokedAdTask(ApiActiveDirectoryController controller, ActiveDirectoryService activeDirectoryService, ApplicationConfigurationService applicationConfigurationService, AdObjectType dataSource) {
        super(controller, activeDirectoryService, applicationConfigurationService, dataSource);
        currentAdTaskType = AdTaskType.FETCH_ETL;
    }

    @Override
    public void run() {
        currentAdTaskType = AdTaskType.FETCH_ETL;
        Thread.currentThread().setName(THREAD_NAME + "_" + currentAdTaskType + "_" + dataSource);

        final boolean succeeded = handleAdTask(currentAdTaskType);
        logger.info("Finished executing Fetch and ETL for datasource {}", dataSource);

        if (!followingTasks.isEmpty()) {
            if (!succeeded) {
                logger.warn("There are following task {}, but task didn't succeed so they will not be executed");
            }
            else {
                logger.info("Running task {}'s following tasks {}", this, followingTasks);
                controller.executeTasks(followingTasks);
            }
        }
    }

}
