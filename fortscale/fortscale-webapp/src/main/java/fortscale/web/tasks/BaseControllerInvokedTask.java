package fortscale.web.tasks;

import fortscale.services.impl.ProcessExecutor;
import fortscale.utils.logging.Logger;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.web.rest.ApiActiveDirectoryController;
import fortscale.web.services.ActivityMonitoringExecutorService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public abstract class BaseControllerInvokedTask {

    private static final Logger logger = Logger.getLogger(BaseControllerInvokedTask.class);

    /**
     * This method creates a new PROCESS and runs the given collection job
     * @param jobName the collection job name for the task (example: User_Fetch)
     * @param jobGroup the collection job group for the task (example: AD)
     * @param additionalParams a map of the additional params (example: resultsId=12345)
     * @return true if the execution finished successfully, false otherwise
     */
    protected boolean runCollectionJob(String jobName, String jobGroup, Map<String, String> additionalParams) {
        String userName = SpringPropertiesUtil.getProperty("user.name");
        final String scriptPath = ApiActiveDirectoryController.COLLECTION_TARGET_DIR + "/resources/scripts/runAdTask.sh"; // this scripts runs the fetch/etl
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }
        final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("/usr/bin/sudo", "-u", userName, scriptPath, jobName , jobGroup, stringBuilder.toString()));
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
