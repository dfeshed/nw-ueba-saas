package presidio.ade.sdk.feature_buckets;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimeRange;

import java.util.Map;

/**
 * Provides the ADE's consumers with APIs related to Feature Buckets.
 *
 * @author Lior Govrin
 */
public interface FeatureBucketsManagerSdk {
    FeatureBucket createFeatureBucketFromEnrichedRecords(String featureBucketConfName, TimeRange timeRange, Map<String, String> context, int pageSize);
}
