package fortscale.collection.jobs.activity;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author gils
 * 23/05/2016
 */
@DisallowConcurrentExecution
public class UserActivityJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(UserActivityJob.class);

    @Autowired
    private UserActivityConfigurationService userActivityConfigurationService;

    @Autowired
    private UserActivityHandlerFactory userActivityHandlerFactory;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Loading Entity Activity Job Parameters..");
        logger.info("Finished Loading Entity Activity Job Parameters");
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false;
    }

    @Override
    public void runSteps() throws Exception {
        logger.info("Start Executing User Activity job..");

        Set<String> activityNames = userActivityConfigurationService.getActivities();

        for (String activity : activityNames) {
            UserActivityLocationsHandler userActivityHandler = userActivityHandlerFactory.createUserActivityHandler(activity);

            userActivityHandler.calculate();
        }
    }
}
