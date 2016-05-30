package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author gils
 * 23/05/2016
 */
@DisallowConcurrentExecution
public class UserActivityJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(UserActivityJob.class);

    @Autowired
    private UserActivityRawDataRetriever userActivityRawDataRetriever;

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

        List<FeatureBucket> buckets = userActivityRawDataRetriever.retrieve("vpn", 1462060800l, 1462320000l);
    }
}
