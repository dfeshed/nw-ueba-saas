package fortscale.acumulator.aggregation;

import fortscale.acumulator.AccumulationParams;
import fortscale.acumulator.Accumulator;
import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.logging.Logger;

import java.time.Duration;
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

    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;


    public AggregatedFeatureEventsAccumulator(AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore,
                                              AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore) {
        this.aggregatedFeatureEventsMongoStore = aggregatedFeatureEventsMongoStore;
        this.accumulatedAggregatedFeatureEventStore = accumulatedAggregatedFeatureEventStore;
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
        long amountOfDays = Duration.between(from, to).get(ChronoUnit.DAYS);
        Instant fromCursor = Instant.from(from);
        String featureName = params.getFeatureName();
        while (fromCursor.isBefore(to)) {
            Instant toCursor = fromCursor.plus(1, ChronoUnit.DAYS);

            if (toCursor.isAfter(to)) {
                List<AggrEvent> aggregatedEvents = aggregatedFeatureEventsMongoStore.findAggrEventsByTimeRange(fromCursor,
                        to, featureName);
                Collection<AccumulatedAggregatedFeatureEvent> accumulatedEvents = accumulateEvents(aggregatedEvents, fromCursor, to);
                accumulatedAggregatedFeatureEventStore.insert(accumulatedEvents, );

                // TODO: 10/6/16 handle aggregated events here
            } else {
                List<AggrEvent> aggregatedEvents = aggregatedFeatureEventsMongoStore.findAggrEventsByTimeRange(fromCursor,
                        toCursor, featureName);
                // TODO: 10/6/16 handle aggregated events here
            }
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

}
