package fortscale.aggregation.feature.bucket;

import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.time.TimeRange;

import java.util.List;
import java.util.Set;

public interface FeatureBucketReader {
	Set<String> getDistinctContextIds(FeatureBucketConf featureBucketConf, TimeRange timeRange);

	List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(String featureBucketConfName, TimeRange timeRange);

	List<FeatureBucket> getFeatureBuckets(String featureBucketConfName, Set<String> contextIds, TimeRange timeRange, int skip, int limit);
}
