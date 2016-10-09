package fortscale.accumulator.aggregation.store;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Created by barak_schuster on 10/6/16.
 */
public interface AccumulatedAggregatedFeatureEventStore {
    void insert(Collection<AccumulatedAggregatedFeatureEvent> event, String featureName);

    Instant getLastAccumulatedEventStartTime(String featureName);

	List<AccumulatedAggregatedFeatureEvent> findAccumulatedEventsByContextIdAndTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf,
			String contextId,
			Instant startTime,
			Instant endTime);
}
