package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Dummy implementation of IAggrFeatureEventService
 * This class should be used when AggregatorManager should not send aggregation events
 * e.g. in step 2 of BDP mode.
 */
@Configurable(preConstruction = true) public class AggrFeatureEventDummyService implements IAggrFeatureEventService {

	public AggrFeatureEventDummyService() {
	}

	@Override public void newFeatureBuckets(List<FeatureBucket> buckets) {
		// do nothing
	}

	@Override public void featureBucketsEndTimeUpdate(List<FeatureBucket> updatedFeatureBucketsWithNewEndTime) {
		// do nothing
	}

	@Override public void sendEvents(long curEventTime) {

	}
}