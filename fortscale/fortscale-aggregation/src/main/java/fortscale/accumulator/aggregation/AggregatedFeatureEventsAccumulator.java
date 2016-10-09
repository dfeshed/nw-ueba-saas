package fortscale.accumulator.aggregation;

import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.accumulator.AccumulatorBase;
import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.aggregation.metrics.AggregatedFeatureEventsAccumulatorMetrics;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
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

/**
 * Accumulates several {@link AggrEvent} into accumulated {@link AccumulatedAggregatedFeatureEvent} per contextId by daily resolution
 * Created by barak_schuster on 10/6/16.
 */
public class AggregatedFeatureEventsAccumulator extends AccumulatorBase {

    private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsAccumulator.class);
    private final AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;
    private final StatsService statsService;
    private Map<String, AggregatedFeatureEventsAccumulatorMetrics> metricsMap;
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    private Map<String, AccumulatedAggregatedFeatureEvent> accumulatedAggregatedFeatureEventMap;

    /**
     * C'tor
     *
     * @param aggregatedFeatureEventsMongoStore
     * @param accumulatedAggregatedFeatureEventStore
     * @param statsService
     */
    public AggregatedFeatureEventsAccumulator(AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore,
                                              AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore,
                                              StatsService statsService) {
        super(logger);
        this.aggregatedFeatureEventsMongoStore = aggregatedFeatureEventsMongoStore;
        this.accumulatedAggregatedFeatureEventStore = accumulatedAggregatedFeatureEventStore;
        this.statsService = statsService;
        this.metricsMap = new HashMap<>();
    }

    /**
     * increase run metrics
     *
     * @param params
     */
    @Override
    protected void beforeRun(AccumulationParams params) {
        AggregatedFeatureEventsAccumulatorMetrics metrics = getMetrics(params.getFeatureName());
        metrics.run++;
    }

    /**
     * retrieve events from original aggr events, accumulate them, write the accumulated result into new collection
     *
     * @param featureName
     * @param fromCursor
     * @param toCursor
     */
    @Override
    public void accumulateEvents(String featureName, final Instant fromCursor, final Instant toCursor) {
        accumulatedAggregatedFeatureEventMap = new HashMap<>();
        AggregatedFeatureEventsAccumulatorMetrics metrics = getMetrics(featureName);
        metrics.fromTime = fromCursor.toEpochMilli();
        metrics.toTime = toCursor.toEpochMilli();
        Instant creationTime = Instant.now();

        Instant fromHourCursor = fromCursor;
        // for each hour in a day - that way we are not uploading full-day of Aggr into the memory
        while (fromHourCursor.isBefore(toCursor)) {
            Instant nextHourCursor = fromHourCursor.plus(1, ChronoUnit.HOURS);
            if (nextHourCursor.isAfter(toCursor)) {
                nextHourCursor = toCursor;
            }
            List<AggrEvent> aggregatedEvents =
                    aggregatedFeatureEventsMongoStore.findAggrEventsByTimeRange(fromHourCursor, nextHourCursor, featureName);
            accumulateEvents(aggregatedEvents, fromCursor, toCursor, creationTime);
            fromHourCursor = nextHourCursor;
        }
        Collection<AccumulatedAggregatedFeatureEvent> accumulatedEvents =
                accumulatedAggregatedFeatureEventMap.values();
        accumulatedAggregatedFeatureEventStore.insert(accumulatedEvents, featureName);
    }

    @Override
    public Instant getLastAccumulatedEventStartTime(String featureName) {
        return accumulatedAggregatedFeatureEventStore.getLastAccumulatedEventStartTime(featureName);
    }

    private void accumulateEvents(List<AggrEvent> aggrEvents, Instant from, Instant to, Instant creationTime) {

        for (AggrEvent event : aggrEvents) {
            String contextId = event.getContextId();

            // create accumulated event for this context if none exists
            if (!accumulatedAggregatedFeatureEventMap.containsKey(contextId)) {
                accumulatedAggregatedFeatureEventMap.put(contextId, new AccumulatedAggregatedFeatureEvent(from, to, contextId, creationTime));
            }
            // get accumulated event for contextId
            AccumulatedAggregatedFeatureEvent accumulatedEvent = accumulatedAggregatedFeatureEventMap.get(contextId);
            // add aggregated feature value to accumulated event
            accumulatedEvent.getAggregatedFeatureValues().add(event.getAggregatedFeatureValue());
        }
    }

    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature metrics
     */
    public AggregatedFeatureEventsAccumulatorMetrics getMetrics(String featureName) {
        if (!metricsMap.containsKey(featureName)) {
            AggregatedFeatureEventsAccumulatorMetrics metrics =
                    new AggregatedFeatureEventsAccumulatorMetrics(statsService, featureName);
            metricsMap.put(featureName, metrics);
        }
        return metricsMap.get(featureName);
    }

}
