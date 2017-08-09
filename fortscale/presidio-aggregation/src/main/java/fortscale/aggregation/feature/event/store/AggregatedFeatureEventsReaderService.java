package fortscale.aggregation.feature.event.store;

import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregatedFeatureEventsReaderService {

	private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;

	private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;

	public AggregatedFeatureEventsReaderService(AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore, AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore) {
		this.aggregatedFeatureEventsMongoStore = aggregatedFeatureEventsMongoStore;
		this.accumulatedAggregatedFeatureEventStore = accumulatedAggregatedFeatureEventStore;
	}

	public Set<String> findDistinctContextsByTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, Date startTime, Date endTime) {

		return aggregatedFeatureEventsMongoStore.findDistinctContextsByTimeRange(
				aggregatedFeatureEventConf, startTime, endTime);
	}
	public Set<String> findDistinctAcmContextsByTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, Date startTime, Date endTime) {

		return accumulatedAggregatedFeatureEventStore.findDistinctContextsByTimeRange(
				aggregatedFeatureEventConf, startTime, endTime);
	}

	public List<AggrEvent> findAggrEventsByContextIdAndTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf,
			String contextId, Date startTime, Date endTime) {

		return aggregatedFeatureEventsMongoStore.findAggrEventsByContextIdAndTimeRange(
				aggregatedFeatureEventConf, contextId, startTime, endTime);
	}

	public long findNumOfAggrEventsByTimeRange(AggregatedFeatureEventConf aggregatedFeatureEventConf,
											   Date startTime,
											   Date endTime) {
		return aggregatedFeatureEventsMongoStore.findNumOfAggrEventsByTimeRange(
				aggregatedFeatureEventConf,
				startTime,
				endTime
		);
	}

	public AggrEvent findAggrEventWithTopKScore(AggregatedFeatureEventConf aggregatedFeatureEventConf,
												Date startTime,
												Date endTime,
												int k) {
		return aggregatedFeatureEventsMongoStore.findAggrEventWithTopKScore(aggregatedFeatureEventConf, startTime, endTime, k);
	}

	public Map<Long, List<AggrEvent>> getDateToTopAggrEvents(AggregatedFeatureEventConf aggregatedFeatureEventConf,
															 Date endTime,
															 int numOfDays,
															 int topK) {
		return aggregatedFeatureEventsMongoStore.getDateToTopAggrEvents(aggregatedFeatureEventConf, endTime, numOfDays, topK);
	}
}
