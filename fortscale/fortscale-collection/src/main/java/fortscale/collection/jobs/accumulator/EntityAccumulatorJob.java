package fortscale.collection.jobs.accumulator;

import fortscale.collection.jobs.FortscaleJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class EntityAccumulatorJob extends FortscaleJob{

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return true;
    }

    @Override
    protected void runSteps() throws Exception {

    }
}
