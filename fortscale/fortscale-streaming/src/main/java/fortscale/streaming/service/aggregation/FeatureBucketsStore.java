package fortscale.streaming.service.aggregation;

import java.util.List;

public interface FeatureBucketsStore {
	// TODO implement API
	public List<FeatureBucket> getFeatureBuckets();
	public void saveFeatureBucket();
}
