package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;

import java.util.List;
import java.util.Map;

/**
 * Service to provide basic query functionality of aggregated events
 *
 * @author Amir Keren
 * Date: 15/11/2015
 */
public interface FeatureBucketQueryService {

    List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(String featureName, String contextType,
                                                               String ContextName, Long startTime, Long endTime);
    FeatureBucket getFeatureBucketsById(String bucketId, String collectionName);
    void addBucket(FeatureBucket bucket, String collectionName);
    void updateBucketFeatureMap(String bucketId, Map<String, Feature> featureMap, String collectionName);

}