package fortscale.collection.jobs.activity;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Loading Entity Activity Job Parameters..");
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
        logger.info("Executing User Activity job..");

        Set<String> activityNames = userActivityConfigurationService.getActivities();

        long startTime = calculateStartTime();
        long endTime = calculateEndTime();

        for (String activity : activityNames) {
            UserActivityLocationsHandler userActivityHandler = userActivityHandlerFactory.createUserActivityHandler(activity);

            userActivityHandler.handle(startTime, endTime, userActivityConfigurationService, mongoTemplate);
        }
    }

    private long calculateStartTime() {
        // TODO
        return 1462060800;
    }

    private long calculateEndTime() {
        // TODO
        return 1464566399;
    }
}
