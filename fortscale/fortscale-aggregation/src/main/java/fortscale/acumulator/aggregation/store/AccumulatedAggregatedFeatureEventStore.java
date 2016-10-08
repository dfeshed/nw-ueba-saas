package fortscale.acumulator.aggregation.store;

import fortscale.acumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;

import java.util.Collection;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedAggregatedFeatureEventStore {
    void insert(Collection<AccumulatedAggregatedFeatureEvent> event, String featureName);

}
