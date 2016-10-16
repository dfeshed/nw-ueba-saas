package fortscale.collection.jobs.accumulator;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatorJob extends FortscaleJob {

    private static final String ACCUMULATE_TYPE_ENTITY_EVENT = "entityEvent";
    private static final String ACCUMULATE_TYPE_AGGR_EVENT = "aggrEvent";
    private static Logger logger = Logger.getLogger(AccumulatorJob.class);

    @Value("${fortscale.accumulator.param.from}")
    private String accumulatorFromParam;
    @Value("${fortscale.accumulator.param.to}")
    private String accumulatorToParam;
    @Value("${fortscale.accumulator.param.featureNames}")
    private String accumulatorFeatureNamesParam;
    @Value("${fortscale.accumulator.param.featureNames.delimiter}")
    private String accumulatorFeatureNamesDelimiter;
    @Value("${fortscale.accumulator.param.from.days.ago}")
    private int accumulatorFromDaysAgo;
    @Value("${fortscale.accumulator.param.type}")
    private String accumulatorTypeParam;

    @Autowired
    private AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager;

    @Autowired
    private EntityEventAccumulatorManagerImpl entityEventAccumulatorManager;

    private AccumulatorManagerParams accumulatorManagerParams;
    private String eventType;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();

        accumulatorManagerParams = new AccumulatorManagerParams();
        if(map.containsKey(accumulatorFromParam)) {
            accumulatorManagerParams.setFrom(jobDataMapExtension.getJobDataMapInstantValue(map, accumulatorFromParam));
        }
        else
        {
            Instant daysAgo = Instant.now().minus(accumulatorFromDaysAgo, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            accumulatorManagerParams.setFrom(daysAgo);
        }

        if(map.containsKey(accumulatorToParam)) {
            accumulatorManagerParams.setTo(jobDataMapExtension.getJobDataMapInstantValue(map, accumulatorToParam));
        }

        if(map.containsKey(accumulatorFeatureNamesParam))
        {
            String featuresStringValue = jobDataMapExtension.getJobDataMapStringValue(map, accumulatorFeatureNamesParam);

            if (featuresStringValue != null) {
                Set<String> features = new HashSet(Arrays.asList(featuresStringValue.split(accumulatorFeatureNamesDelimiter)));
                accumulatorManagerParams.setFeatures(features);
            }
        }
        String missingParamsErrMessage = String.format("param %s is missing. possible values: %s,%s",
                accumulatorTypeParam,ACCUMULATE_TYPE_ENTITY_EVENT,ACCUMULATE_TYPE_AGGR_EVENT);
        Assert.isTrue(map.containsKey(accumulatorTypeParam), missingParamsErrMessage);

        eventType = jobDataMapExtension.getJobDataMapStringValue(map, accumulatorTypeParam);
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
        startNewStep("Accumulator Job");
        try {
            if(eventType.equals(ACCUMULATE_TYPE_ENTITY_EVENT))
            {
                entityEventAccumulatorManager.run(accumulatorManagerParams);
            }

            else if (eventType.equals(ACCUMULATE_TYPE_AGGR_EVENT))
            {
                aggregatedFeatureEventsAccumulatorManager.run(accumulatorManagerParams);
            }
        }
        catch (Exception e)
        {
            logger.error("got exception while performing accumulator job",e);
        }

        finishStep();
    }
}
