package fortscale.aggregation.feature.bucket;

import fortscale.utils.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class FeatureBucketsInMemory implements FeatureBucketsStore {

    private static final Logger logger = Logger.getLogger(FeatureBucketsInMemory.class);

    //map of bucketId to FeatureBucket
    private Map<String, FeatureBucket> memoryStore;

    public FeatureBucketsInMemory() {
        memoryStore = new HashMap<>();
    }

    @Override
    public void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket) throws Exception {
        memoryStore.put(featureBucket.getBucketId(), featureBucket);
    }

    @Override
    public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf, String bucketId) {
        return memoryStore.get(bucketId);
    }

    @Override
    public void clearAll() {
        memoryStore.clear();
    }


    @Override
    public List<FeatureBucket> getAllFeatureBuckets() {
        return memoryStore.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
        return null;
    }
}
