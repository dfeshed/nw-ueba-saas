package fortscale.collection.jobs.accumulator;

import fortscale.accumulator.accumulator.AccumulationParams.TimeFrame;
import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.accumulator.manager.AccumulatorManger;
import fortscale.collection.jobs.FortscaleJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatorJob extends FortscaleJob{

    @Value("${fortscale.accumulator.param.from}")
    String accumulateFromParam;
    @Value("${fortscale.accumulator.param.to}")
    String accumulateToParam;
    @Value("${fortscale.accumulator.param.featureNames}")
    String accumulateFeatureNamesParam;
    @Value("${fortscale.accumulator.param.featureNames.delimiter}")
    String accumulateFeatureNamesDelimiter;
    @Value("${fortscale.accumulator.param.timeFrame}")
    TimeFrame accumulateTimeFrame;

    @Autowired
    AccumulatorManger accumulatorManger;


    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        if(map.containsKey(accumulateFromParam))
        {
            jobDataMapExtension.getJobDataMapInstantValue(map,accumulateFromParam);
        }
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();

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
