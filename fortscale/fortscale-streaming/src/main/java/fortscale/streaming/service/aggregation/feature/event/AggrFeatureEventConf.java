package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.FeatureBucket;
import fortscale.streaming.service.aggregation.FeatureBucketConf;

import java.util.List;

/**
 * Created by amira on 08/07/2015.
 */
public class AggrFeatureEventConf {
    private FeatureBucketConf featureBucketConf;
    private int numberOfBuckets;
    private int bucketLeap;
    private String name;
    private static String aggrFeatureFuncJson;

    public static String getAggrFeatureFuncJson() {
        return aggrFeatureFuncJson;
    }

    public int getBucketLeap() {
        return bucketLeap;
    }

    public FeatureBucketConf getFeatureBucketConf() {
        return featureBucketConf;
    }

    public int getNumberOfBuckets() {
        return numberOfBuckets;
    }

    public String getName() {
        return name;
    }

    public List<String> getFeatureNames() {
        return null;
    }
}
