package fortscale.web.tasks;

import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;
import fortscale.web.services.ActivityMonitoringExecutorService;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexp on 16/02/2017.
 */
public abstract class BaseControllerInvokedTask {

    private static final Logger logger = Logger.getLogger(BaseControllerInvokedTask.class);

    /**
     * This method creates a new PROCESS and runs the given collection job
     * @param jobName the collection job name for the task (example User_Fetch AD)
     * @param resultsId the random id that will be given to this job execution results in application configuration
     * @return true if the execution finished successfully, false otherwise
     */
    protected boolean runCollectionJob(String jobName, String resultsId, String jobGroup) {
        Process process;
        try {
            final String scriptPath = ApiActiveDirectoryController.COLLECTION_TARGET_DIR + "/resources/scripts/runAdTask.sh"; // this scripts runs the fetch/etl
            final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("/usr/bin/sudo", "-u", "cloudera", scriptPath, jobName , jobGroup, "resultsId="+resultsId));
            final ProcessBuilder processBuilder = new ProcessBuilder(arguments).redirectErrorStream(true);
            processBuilder.directory(new File(ApiActiveDirectoryController.COLLECTION_TARGET_DIR));
            processBuilder.redirectErrorStream(true);
            logger.debug("Starting process with arguments {}", arguments);
            process = processBuilder.start();
        } catch (IOException e) {
            logger.error("Execution of task {} has failed.", jobName, e);
            return false;
        }
        int status;
        try {
            status = process.waitFor();
        } catch (InterruptedException e) {
            if (process.isAlive()) {
                logger.error("Killing the process forcibly");
                process.destroyForcibly();
            }
            logger.error("Execution of task {} has failed. Task has been interrupted", jobName, e);
            return false;
        }

        if (status != 0) {
            try {
                String processOutput = IOUtils.toString(process.getInputStream());
                final int length = processOutput.length();
                if (length > 1000) {
                    processOutput = processOutput.substring(length - 1000, length); // getting last 1000 chars to not overload the log file
                }
                logger.error("Error stream for job {} = \n{}", jobName, processOutput);
            } catch (IOException e) {
                logger.warn("Failed to get error stream from process for job {}", jobName);
            }
            logger.error("Execution of task {} has finished with status {}. Execution failed", jobName, status);
            return false;
        }

        logger.debug("Execution of task {} has finished with status {}", jobName, status);
        return true;
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
