package fortscale.acumulator.aggregation;

import fortscale.acumulator.AccumulationParams;
import fortscale.acumulator.Accumulator;
import fortscale.acumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.acumulator.aggregation.metrics.AggregatedFeatureEventsAccumulatorMetrics;
import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.acumulator.AccumulationParams.TimeFrame.DAILY;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class AggregatedFeatureEventsAccumulator implements Accumulator {

    private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsAccumulator.class);
    private final AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;
    private final StatsService statsService;
    private Map<String,AggregatedFeatureEventsAccumulatorMetrics> metricsMap;
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;

    /**
     * C'tor
     * @param aggregatedFeatureEventsMongoStore
     * @param accumulatedAggregatedFeatureEventStore
     * @param statsService
     */
    public AggregatedFeatureEventsAccumulator(AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore,
                                              AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore,
                                              StatsService statsService) {
        this.aggregatedFeatureEventsMongoStore = aggregatedFeatureEventsMongoStore;
        this.accumulatedAggregatedFeatureEventStore = accumulatedAggregatedFeatureEventStore;
        this.statsService = statsService;
        this.metricsMap = new HashMap<>();
    }

    @Override
    public void run(AccumulationParams params) {
        logger.info("running AggregatedFeatureEvents accumulation by params={}", params);
        AccumulationParams.TimeFrame timeFrame = params.getTimeFrame();
        if (!timeFrame.equals(DAILY)) {
            throw new UnsupportedOperationException(String.format(
                    "%s does not support accumulation of timeFrame=%s",
                    getClass().getSimpleName(), timeFrame));
        }

        Instant from = params.getFrom();
        Instant to = params.getTo();
        Instant fromCursor = Instant.from(from);
        String featureName = params.getFeatureName();
        AggregatedFeatureEventsAccumulatorMetrics metrics = getMetrics(featureName);
        metrics.run++;

        while (fromCursor.isBefore(to)) {
            Instant toCursor;

            if(fromCursor.plus(1, ChronoUnit.DAYS).isAfter(to))
            {
                toCursor = to;
            }
            else
            {
                toCursor =  fromCursor.plus(1, ChronoUnit.DAYS);
            }
            metrics.fromTime = fromCursor.toEpochMilli();
            metrics.toTime = toCursor.toEpochMilli();
            List<AggrEvent> aggregatedEvents = aggregatedFeatureEventsMongoStore.findAggrEventsByTimeRange(fromCursor,
                    toCursor, featureName);
            Collection<AccumulatedAggregatedFeatureEvent> accumulatedEvents = accumulateEvents(aggregatedEvents, fromCursor, to);
            accumulatedAggregatedFeatureEventStore.insert(accumulatedEvents,featureName );

            fromCursor = toCursor.plusMillis(1);
        }
    }

    private Collection<AccumulatedAggregatedFeatureEvent> accumulateEvents(List<AggrEvent> aggrEvents, Instant from, Instant to) {
        Instant creationTime = Instant.now();
        Map<String, AccumulatedAggregatedFeatureEvent> accumulatedAggregatedFeatureEventMap = new HashMap<>();
        for (AggrEvent event : aggrEvents) {
            String contextId = event.getContextId();

            // create accumulated event for this context if none exists
            if (!accumulatedAggregatedFeatureEventMap.containsKey(contextId)) {
                accumulatedAggregatedFeatureEventMap.put(contextId, new AccumulatedAggregatedFeatureEvent(from,to,contextId,creationTime));
            }
            // get accumulated event for contextId
            AccumulatedAggregatedFeatureEvent accumulatedEvent = accumulatedAggregatedFeatureEventMap.get(contextId);
            // add aggregated feature value to accumulated event
            accumulatedEvent.getAggregatedFeatureValues().add(event.getAggregatedFeatureValue());
        }
        return accumulatedAggregatedFeatureEventMap.values();
    }

    /**
     * creates metrics for feature if none exists and returns it
     * @param featureName
     * @return feature metrics
     */
    public AggregatedFeatureEventsAccumulatorMetrics getMetrics(String featureName)
    {
        if(!metricsMap.containsKey(featureName))
        {
            AggregatedFeatureEventsAccumulatorMetrics metrics =
                    new AggregatedFeatureEventsAccumulatorMetrics(statsService, featureName);
            metricsMap.put(featureName,metrics);
        }
        return metricsMap.get(featureName);
    }

}
