package fortscale.aggregation.feature.bucket.repository.state;

/**
 * Created by alexp on 11/12/16.
 */
public interface FeatureBucketStateService {
    void updateFeatureBucketState(long lastEventEpochtime);
    FeatureBucketState getFeatureBucketState();
}
