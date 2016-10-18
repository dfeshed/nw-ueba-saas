package fortscale.accumulator.manager;

import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.accumulator.Accumulator;
import fortscale.accumulator.entityEvent.EntityEventAccumulator;
import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Created by barak_schuster on 10/9/16.
 */
public abstract class AccumulatorManagerImpl implements AccumulatorManger {
    private static final Logger logger = Logger.getLogger(AccumulatorManagerImpl.class);

    private final Accumulator accumulator;

    /**
     * C'tor
     *
     * @param accumulator accumulator to execute by
     * @see fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulator
     * @see EntityEventAccumulator
     */
    public AccumulatorManagerImpl(Accumulator accumulator) {
        this.accumulator = accumulator;
    }

    public void run(AccumulatorManagerParams params) {
        Set<String> features;
        if(params.getFeatures()!=null)
        {
            features = params.getFeatures();
        }
        else
        {
            features = getFeatureNames();
        }
        for (String feature : features) {
            Instant accumulateFrom = calcFromInstant(params, feature);
            Instant accumulateTo = getToInstant(params, feature);
            if(accumulateTo==null)
            {
                logger.info("found 0 events to accumulate for feature={}", feature);
                return;
            }
            AccumulationParams accumulationParams = new AccumulationParams(feature, AccumulationParams.TimeFrame.DAILY, accumulateFrom, accumulateTo);
            accumulator.run(accumulationParams);
        }
    }

    /**
     *
     * @return
     */
    protected abstract Set<String> getFeatureNames();

    private Instant getToInstant(AccumulatorManagerParams params, String feature) {
        Instant accumulateTo;
        if (params.getTo() == null) {
            accumulateTo = Instant.now();
        } else {
            accumulateTo = params.getTo();
        }
        Instant lastSourceEventStartTime = getLastSourceEventStartTime(feature);
        // nothing to accumulate
        if(lastSourceEventStartTime == null)
        {
            return null;
        }
        if(accumulateTo.isAfter(lastSourceEventStartTime))
        {
            accumulateTo = lastSourceEventStartTime;
        }
        return accumulateTo.truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
    }

    private Instant calcFromInstant(AccumulatorManagerParams params, String feature) {
        Instant accumulateFrom;
        Instant lastAccumulatedEvent = getLastAccumulatedEventTime(feature);
        if (lastAccumulatedEvent != null) {
            if (lastAccumulatedEvent.isAfter(params.getFrom())) {
                accumulateFrom = lastAccumulatedEvent.plus(1,ChronoUnit.DAYS);
            } else {
                accumulateFrom = params.getFrom();
            }
        } else {
            accumulateFrom = params.getFrom();
        }
        return accumulateFrom.truncatedTo(ChronoUnit.DAYS);
    }

    /**
     * gets the last accumulated event for feature. all accumulations will occur from this point forward
     * @param featureName
     * @return
     */
    private Instant getLastAccumulatedEventTime(String featureName) {
        return accumulator.getLastAccumulatedEventStartTime(featureName);
    }

    private Instant getLastSourceEventStartTime(String featureName)
    {
        return accumulator.getLastSourceEventStartTime(featureName);
    }
}
