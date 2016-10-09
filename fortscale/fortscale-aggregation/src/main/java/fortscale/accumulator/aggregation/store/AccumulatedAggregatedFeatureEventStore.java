package fortscale.accumulator.aggregation.store;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;

import java.time.Instant;
import java.util.Collection;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedAggregatedFeatureEventStore {
    void insert(Collection<AccumulatedAggregatedFeatureEvent> event, String featureName);

    Instant getLastAccumulatedEventStartTime(String featureName);
}
