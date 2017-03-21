package fortscale.web.tasks;

import fortscale.services.impl.ProcessExecutor;
import fortscale.utils.logging.Logger;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.web.rest.ApiActiveDirectoryController;
import fortscale.web.services.ActivityMonitoringExecutorService;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public abstract class BaseControllerInvokedTask {

    private static final Logger logger = Logger.getLogger(BaseControllerInvokedTask.class);

    /**
     * This method creates a new PROCESS and runs the given collection job
     * @param jobName the collection job name for the task (example User_Fetch AD)
     * @param resultsId the random id that will be given to this job execution results in application configuration
     * @return true if the execution finished successfully, false otherwise
     */
    protected boolean runCollectionJob(String jobName, String resultsId, String jobGroup) {
        String userName = SpringPropertiesUtil.getProperty("user.name");
        final String scriptPath = ApiActiveDirectoryController.COLLECTION_TARGET_DIR + "/resources/scripts/runAdTask.sh"; // this scripts runs the fetch/etl
        final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("/usr/bin/sudo", "-u", userName, scriptPath, jobName , jobGroup, "resultsId="+resultsId));
        return ProcessExecutor.executeProcess(jobName, arguments, ApiActiveDirectoryController.COLLECTION_TARGET_DIR);
    }

    protected void notifyTaskStart() {
        if (!getActivityMonitoringExecutorService().markTaskActive(this)) {
            logger.warn("Tried to add task {} but the task already exists.", this);
        }
        else {
            logger.debug("added running task {} to active tasks", this);
        }
    }

    protected void notifyTaskDone() {
        if (!getActivityMonitoringExecutorService().markTaskInactive(this)) {
            logger.warn("Tried to remove task {} but task doesn't exist.", this);
        }
        else {
            logger.debug("Removed running task {} from active tasks", this);
        }
    }
    protected abstract ActivityMonitoringExecutorService getActivityMonitoringExecutorService();

}
