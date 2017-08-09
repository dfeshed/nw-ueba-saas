package fortscale.aggregation.feature.bucket;

import java.util.List;

/**
 * Created by YaronDL on 6/29/2017.
 */
public interface FeatureBucketsAggregatorStore {
    public void storeFeatureBucket(FeatureBucket featureBucket);
    public FeatureBucket getFeatureBucket(String bucketId);
}
