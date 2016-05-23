package fortscale.collection.jobs.activity;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author gils
 * 23/05/2016
 */
@DisallowConcurrentExecution
public class EntityActivityJob extends FortscaleJob {

    private static Logger logger = Logger.getLogger(EntityActivityJob.class);

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
    protected void runSteps() throws Exception {
        logger.info("Executing EntityActivity job..");
    }
}
