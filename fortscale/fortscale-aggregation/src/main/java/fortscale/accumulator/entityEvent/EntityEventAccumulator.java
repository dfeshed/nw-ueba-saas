package fortscale.accumulator.entityEvent;

import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.accumulator.BaseAccumulator;
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
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.DAILY_FEATURE_SUFFIX;
import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.HOURLY_FEATURE_SUFFIX;
import static fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService.buildFullAggregatedFeatureEventName;
import static fortscale.utils.time.TimeUtils.epochSecondsToHourOfDay;

/**
 * Accumulates several {@link EntityEvent} into accumulated {@link AccumulatedEntityEvent} per contextId by daily resolution
 * Created by barak_schuster on 10/6/16.
 */
public class EntityEventAccumulator extends BaseAccumulator {
    private static final Logger logger = Logger.getLogger(EntityEventAccumulator.class);
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_IN_A_DAY = 1;
    private final EntityEventMongoStore entityEventMongoStore;
    private final AccumulatedEntityEventStore accumulatedEntityEventStore;
    private final StatsService statsService;
    private Map<String, EntityEventAccumulatorMetrics> metricsMap;

    /**
     * C'tor
     * @param entityEventMongoStore
     * @param accumulatedEntityEventStore
     * @param statsService
     * @param defaultFromPeriodDaily if accumulation is ran without from params, daily accumulation start from now-this period
     * @param defaultFromPeriodHourly if accumulation is ran without from params, hourly accumulation start from now-this period
     */
    public EntityEventAccumulator(EntityEventMongoStore entityEventMongoStore,
                                  AccumulatedEntityEventStore accumulatedEntityEventStore,
                                  StatsService statsService, Period defaultFromPeriodDaily,
                                  Period defaultFromPeriodHourly) {
        super(logger, defaultFromPeriodDaily, defaultFromPeriodHourly);
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
        Map<String, AccumulatedEntityEvent> accumulatedEntityEventMap = new HashMap<>();

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
                    entityEventMongoStore.findEntityEventsByStartTimeRange(fromHourCursor, nextHourCursor, featureName);
            accumulateEvents(entityEvents, fromCursor, toCursor, creationTime, accumulatedEntityEventMap);
            fromHourCursor = nextHourCursor;
        }
        Collection<AccumulatedEntityEvent> accumulatedEvents =
                accumulatedEntityEventMap.values();
        if(!accumulatedEvents.isEmpty()) {
            accumulatedEntityEventStore.insert(accumulatedEvents, featureName);
        }
    }

    @Override
    public Instant getLastAccumulatedEventStartTime(String featureName) {
        return accumulatedEntityEventStore.getLastAccumulatedEventStartTime(featureName);
    }

    @Override
    public Instant getLastSourceEventStartTime(String featureName) {
        return entityEventMongoStore.getLastEntityEventStartTime(featureName);
    }

    private void accumulateEvents(List<EntityEvent> events, Instant from, Instant to, Instant creationTime,
                                  Map<String, AccumulatedEntityEvent> accumulatedEntityEventMap) {

        for (EntityEvent entityEvent : events) {
            String contextId = entityEvent.getContextId();

            // get accumulated event for contextId
            AccumulatedEntityEvent accumulatedEvent = accumulatedEntityEventMap.get(contextId);

            // create accumulated event for this context if none exists
            if(accumulatedEvent == null)
            {
                accumulatedEvent = new AccumulatedEntityEvent(from, to, contextId, creationTime);
                accumulatedEntityEventMap.put(contextId, accumulatedEvent);
            }

            Map<String,Map<Integer,Double>>  aggregatedFeatureEventsValuesMap = accumulatedEvent.getAggregated_feature_events_values_map();
            Set<Integer> activityTimeSet = accumulatedEvent.getActivityTime();

            for (JSONObject aggrEvent : entityEvent.getAggregated_feature_events()) {

                String featureName = aggrEvent.getAsString(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_NAME);
                String bucketName = aggrEvent.getAsString(AggrEvent.EVENT_FIELD_BUCKET_CONF_NAME);
                String fullAggregatedFeatureEventName= buildFullAggregatedFeatureEventName(bucketName,featureName);

                long eventStartTimeEpochSeconds = aggrEvent.getAsNumber(AggrEvent.EVENT_FIELD_START_TIME_UNIX).longValue();
                int eventHourOfDay = epochSecondsToHourOfDay(eventStartTimeEpochSeconds);

                activityTimeSet.add(eventHourOfDay);

                Map<Integer,Double> hourToScoreMap = aggregatedFeatureEventsValuesMap.get(fullAggregatedFeatureEventName);
                if (hourToScoreMap == null)
                {
                    hourToScoreMap = new HashMap<>();

                    aggregatedFeatureEventsValuesMap.put(fullAggregatedFeatureEventName, hourToScoreMap);
                }

                String featureType = aggrEvent.getAsString(AggrEvent.EVENT_FIELD_FEATURE_TYPE);
                switch (featureType) {
                    case "F":
                        hourToScoreMap.put(eventHourOfDay, aggrEvent.getAsNumber(AggrEvent.EVENT_FIELD_SCORE).doubleValue());
                        break;
                    case "P":
                        hourToScoreMap.put(eventHourOfDay, aggrEvent.getAsNumber(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE).doubleValue());
                        break;
                    default:
                        String message = String.format("cannot accumulate entityEvent with aggrFeatureEvent of type=%s", featureType);
                        throw new UnsupportedOperationException(message);
                }
            }
        }

    }


    /**
     * creates metrics for feature if none exists and returns it
     *
     * @param featureName
     * @return feature metrics
     */
    public EntityEventAccumulatorMetrics getMetrics(String featureName) {
        EntityEventAccumulatorMetrics metrics = metricsMap.get(featureName);
        if (metrics == null)
        {
            metrics = new EntityEventAccumulatorMetrics(statsService, featureName);
            metricsMap.put(featureName, metrics);
        }

        return metrics;
    }

}
