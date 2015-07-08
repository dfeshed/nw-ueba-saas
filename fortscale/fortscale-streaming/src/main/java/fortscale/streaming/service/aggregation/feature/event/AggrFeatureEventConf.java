package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.FeatureBucket;
import fortscale.streaming.service.aggregation.FeatureBucketConf;

/**
 * Created by amira on 08/07/2015.
 */
public class AggrFeatureEventConf {
    private FeatureBucketConf featureBucketConf;
    private int numberOfBuckets;

    public FeatureBucketConf getFeatureBucketConf() {
        return featureBucketConf;
    }

    public int getNumberOfBuckets() {
        return numberOfBuckets;
    }
}
