package fortscale.aggregation.feature.event.store;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class AggregatedFeatureEventsReaderService {
	@Autowired
	private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;

	public List<String> findDistinctContextsByTimeRange(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, Date startTime, Date endTime) {

		return aggregatedFeatureEventsMongoStore.findDistinctContextsByTimeRange(
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
}
