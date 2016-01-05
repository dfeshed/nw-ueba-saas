package fortscale.aggregation.feature.bucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FeatureBucketsReaderService {

    @Autowired
    private FeatureBucketsMongoStore featureBucketStrategyStore;

    public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
        return featureBucketStrategyStore.getFeatureBucketsByContextAndTimeRange(featureBucketConf, contextType, ContextName, bucketStartTime, bucketEndTime);
    }

    public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId){
        return featureBucketStrategyStore.getFeatureBucket(featureBucketConf, bucketId);
    }

    public List<FeatureBucket> getFeatureBucketsByTimeRange(FeatureBucketConf featureBucketConf, Long bucketStartTime, Long bucketEndTime, Pageable pageable) {
        return featureBucketStrategyStore.getFeatureBucketsByTimeRange(featureBucketConf, bucketStartTime, bucketEndTime, pageable);
    }
}
