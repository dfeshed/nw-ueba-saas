package fortscale.streaming.service.aggregation.bucket.strategy;

/**
 * Created by amira on 08/07/2015.
 */
public interface NextBucketEndTimeListener {
    void nextBucketEndTimeUpdate(FeatureBucketStrategyData strategyData);
}
