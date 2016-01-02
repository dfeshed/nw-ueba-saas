package fortscale.aggregation.feature.event;

import java.util.List;

import fortscale.aggregation.feature.bucket.FeatureBucket;

public interface IAggrFeatureEventService {
	public void newFeatureBuckets(List<FeatureBucket> buckets);
	public void featureBucketsEndTimeUpdate(List<FeatureBucket> updatedFeatureBucketsWithNewEndTime);
	public void sendEvents(long curEventTime);
}
