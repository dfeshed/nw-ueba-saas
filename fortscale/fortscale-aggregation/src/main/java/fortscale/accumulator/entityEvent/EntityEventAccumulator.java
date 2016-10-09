package fortscale.accumulator.entityEvent;

import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.accumulator.AccumulatorBase;
import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.metrics.EntityEventAccumulatorMetrics;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Accumulates several {@link EntityEvent} into accumulated {@link AccumulatedEntityEvent} per contextId by daily resolution
 * Created by barak_schuster on 10/6/16.
 */
public class EntityEventAccumulator extends AccumulatorBase {
    private static final Logger logger = Logger.getLogger(EntityEventAccumulator.class);
    private final EntityEventMongoStore entityEventMongoStore;
    private final AccumulatedEntityEventStore accumulatedEntityEventStore;
    private final StatsService statsService;
    private Map<String, EntityEventAccumulatorMetrics> metricsMap;
    private Map<String, AccumulatedEntityEvent> accumulatedEntityEventMap;

    public EntityEventAccumulator(EntityEventMongoStore entityEventMongoStore,
                                  AccumulatedEntityEventStore accumulatedEntityEventStore,
                                  StatsService statsService) {
        super(logger);
        this.entityEventMongoStore = entityEventMongoStore;
        this.accumulatedEntityEventStore = accumulatedEntityEventStore;
        this.statsService = statsService;
        this.metricsMap = new HashMap<>();
    }

    @Override
    protected void beforeRun(AccumulationParams params) {
        EntityEventAccumulatorMetrics metrics = getMetrics(params.getFeatureName());
        metrics.run++;
    }

    @Override
    public void accumulateEvents(String featureName, final Instant fromCursor, final Instant toCursor) {
        accumulatedEntityEventMap = new HashMap<>();

        EntityEventAccumulatorMetrics metrics = getMetrics(featureName);
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
            List<EntityEvent> entityEvents =
                    entityEventMongoStore.findEntityEventsByTimeRange(fromHourCursor, nextHourCursor, featureName);
            accumulateEvents(entityEvents, fromCursor, toCursor, creationTime);
            fromHourCursor = nextHourCursor;
        }
        Collection<AccumulatedEntityEvent> accumulatedEvents =
                accumulatedEntityEventMap.values();
        accumulatedEntityEventStore.insert(accumulatedEvents, featureName);
    }

    @Override
    public Instant getLastAccumulatedEventStartTime(String featureName) {
        return accumulatedEntityEventStore.getLastAccumulatedEventStartTime(featureName);
    }

    private Collection<AccumulatedEntityEvent> accumulateEvents(List<EntityEvent> events, Instant from, Instant to, Instant creationTime) {

        for (EntityEvent entityEvent : events) {
            String contextId = entityEvent.getContextId();
            // create accumulated event for this context if none exists
            if (!accumulatedEntityEventMap.containsKey(contextId)) {
                accumulatedEntityEventMap.put(contextId, new AccumulatedEntityEvent(from, to, contextId, creationTime));
            }

            // get accumulated event for contextId
            AccumulatedEntityEvent accumulatedEvent = accumulatedEntityEventMap.get(contextId);

            for (JSONObject aggrEvent : entityEvent.getAggregated_feature_events()) {

                String featureName = aggrEvent.getAsString(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_NAME);
                Map<String, List<Double>> aggregatedFeatureEventsValuesMap = accumulatedEvent.getAggregatedFeatureEventsValuesMap();
                if (!aggregatedFeatureEventsValuesMap.containsKey(featureName)) {
                    aggregatedFeatureEventsValuesMap.put(featureName, new ArrayList<>());
                }
                List<Double> scoreList = aggregatedFeatureEventsValuesMap.get(featureName);

                String featureType = aggrEvent.getAsString(AggrEvent.EVENT_FIELD_FEATURE_TYPE);
                switch (featureType) {
                    case "F":
                        scoreList.add(aggrEvent.getAsNumber(AggrEvent.EVENT_FIELD_SCORE).doubleValue());
                        break;
                    case "P":
                        scoreList.add(aggrEvent.getAsNumber(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE).doubleValue());
                        break;
                    default:
                        String message = String.format("cannot accumulate entityEvent with aggrFeatureEvent of type=%s", featureType);
                        throw new UnsupportedOperationException(message);
                }
            }
        }
        return accumulatedEntityEventMap.values();

    }


    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature metrics
     */
    public EntityEventAccumulatorMetrics getMetrics(String featureName) {
        if (!metricsMap.containsKey(featureName)) {
            EntityEventAccumulatorMetrics metrics =
                    new EntityEventAccumulatorMetrics(statsService, featureName);
            metricsMap.put(featureName, metrics);
        }
        return metricsMap.get(featureName);
    }

}
