package fortscale.aggregation.feature.event.store;

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
}
