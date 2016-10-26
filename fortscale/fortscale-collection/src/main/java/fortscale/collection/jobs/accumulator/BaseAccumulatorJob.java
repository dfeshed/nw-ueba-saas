package fortscale.collection.jobs.accumulator;

import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by barak_schuster on 10/6/16.
 */
public abstract class BaseAccumulatorJob extends FortscaleJob {
    private static Logger logger = Logger.getLogger(BaseAccumulatorJob.class);

    @Value("${fortscale.accumulator.param.from}")
    private String accumulatorFromParam;
    @Value("${fortscale.accumulator.param.to}")
    private String accumulatorToParam;
    @Value("${fortscale.accumulator.param.featureNames}")
    private String accumulatorFeatureNamesParam;
    @Value("${fortscale.accumulator.param.featureNames.delimiter}")
    private String accumulatorFeatureNamesDelimiter;

    protected AccumulatorManagerParams accumulatorManagerParams;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();

        accumulatorManagerParams = new AccumulatorManagerParams();

        try {
            accumulatorManagerParams.setFrom(jobDataMapExtension.getJobDataMapInstantValue(map, accumulatorFromParam, null));
        } catch (DateTimeParseException de) {
            logger.error("error while parsing param={}", accumulatorFromParam, de);
            throw de;
        }

        try {
            accumulatorManagerParams.setTo(jobDataMapExtension.getJobDataMapInstantValue(map, accumulatorToParam, null));
        } catch (DateTimeParseException de) {
            logger.error("error while parsing param={}", accumulatorToParam, de);
            throw de;
        }

        String featuresStringValue = jobDataMapExtension.getJobDataMapStringValue(map, accumulatorFeatureNamesParam,null);
        if(featuresStringValue!=null)
        {
            Set<String> features = new HashSet(Arrays.asList(featuresStringValue.split(accumulatorFeatureNamesDelimiter)));
            accumulatorManagerParams.setFeatures(features);
        }
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
        startNewStep("Accumulator Job");
        try {
            runAccumulation();
        }
        catch (Exception e)
        {
            logger.error("got exception while performing accumulator job",e);
        }

        finishStep();
    }

    /**
     * executes accumulation via inherited-class specified accumulator
     */
    public abstract void runAccumulation();

    /**
     * Setter used for tests
     * @param accumulatorManagerParams
     */
    public void setAccumulatorManagerParams(AccumulatorManagerParams accumulatorManagerParams) {
        this.accumulatorManagerParams = accumulatorManagerParams;
    }
}
