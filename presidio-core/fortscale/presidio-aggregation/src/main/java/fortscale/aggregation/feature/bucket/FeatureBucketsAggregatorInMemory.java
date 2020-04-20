package fortscale.aggregation.feature.bucket;

import fortscale.utils.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class FeatureBucketsAggregatorInMemory implements FeatureBucketsAggregatorStore {

    private static final Logger logger = Logger.getLogger(FeatureBucketsAggregatorInMemory.class);

    //map of bucketId to FeatureBucket
    private Map<String, FeatureBucket> memoryStore;

    public FeatureBucketsAggregatorInMemory() {
        memoryStore = new HashMap<>();
    }

    @Override
    public void storeFeatureBucket(FeatureBucket featureBucket){
        memoryStore.put(featureBucket.getBucketId(), featureBucket);
    }

    @Override
    public FeatureBucket getFeatureBucket(String bucketId) {
        return memoryStore.get(bucketId);
    }

    public List<FeatureBucket> getAllFeatureBuckets() {
        return memoryStore.values().stream().collect(Collectors.toList());
    }
}
