package fortscale.acumulator.aggregation.store;

import fortscale.acumulator.aggregation.AccumulatedAggregatedFeatureEvent;

import java.util.List;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedAggregatedFeatureEventStore {
    void insert(List<AccumulatedAggregatedFeatureEvent> event);

}
