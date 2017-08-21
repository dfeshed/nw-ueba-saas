package fortscale.aggregation.feature.bucket;

import fortscale.utils.time.TimeRange;

import java.util.List;
import java.util.Set;

/**
 * These are all the read only APIs for a {@link FeatureBucket} store.
 *
 * @author Lior Govrin
 */
public interface FeatureBucketReader {
	/**
	 * Get a set of all the distinct context IDs of the {@link FeatureBucket}s
	 * created from the given {@link FeatureBucketConf} in the given {@link TimeRange}.
	 *
	 * @param featureBucketConf the {@link FeatureBucketConf} from which the {@link FeatureBucket}s were created
	 * @param timeRange         the {@link TimeRange} of the {@link FeatureBucket}s
	 * @return a set of distinct context IDs
	 */
	Set<String> getDistinctContextIds(FeatureBucketConf featureBucketConf, TimeRange timeRange);

	/**
	 * Get a list of all the {@link FeatureBucket}s created from the {@link FeatureBucketConf}
	 * whose name is "featureBucketConfName", that belong to the context IDs in the given set,
	 * and that are in the given {@link TimeRange}.
	 *
	 * @param featureBucketConfName the name of the configuration from which the {@link FeatureBucket}s were created
	 * @param contextIds            the {@link FeatureBucket}s should belong to these context IDs
	 * @param timeRange             the {@link TimeRange} of the {@link FeatureBucket}s
	 * @return a list of {@link FeatureBucket}s
	 */
	List<FeatureBucket> getFeatureBuckets(String featureBucketConfName, Set<String> contextIds, TimeRange timeRange);
	List<FeatureBucket> getFeatureBuckets(String featureBucketConfName, String contextIds, TimeRange timeRange);
}
