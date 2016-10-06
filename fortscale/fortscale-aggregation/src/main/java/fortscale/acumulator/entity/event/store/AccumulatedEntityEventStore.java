package fortscale.acumulator.entity.event.store;

import fortscale.acumulator.aggregation.AccumulatedAggregatedFeatureEvent;

import java.util.List;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedEntityEventStore {
    void insert(List<AccumulatedAggregatedFeatureEvent> event);
}
