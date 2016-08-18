package fortscale.aggregation.feature.bucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FeatureBucketsReaderService {

    @Autowired
    private FeatureBucketsMongoStore featureBucketsMongoStore;

    public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
        return featureBucketsMongoStore.getFeatureBucketsByContextAndTimeRange(featureBucketConf, contextType, ContextName, bucketStartTime, bucketEndTime);
    }

    public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId){
        return featureBucketsMongoStore.getFeatureBucket(featureBucketConf, bucketId);
    }

    public List<FeatureBucket> getFeatureBucketsByTimeRange(FeatureBucketConf featureBucketConf, Long bucketStartTime, Long bucketEndTime, Pageable pageable) {
        return featureBucketsMongoStore.getFeatureBucketsByEndTimeBetweenTimeRange(featureBucketConf, bucketStartTime, bucketEndTime, pageable);
    }

    public List<String> findDistinctContextByTimeRange(FeatureBucketConf featureBucketConf, Long startTime, Long endTime){
        return featureBucketsMongoStore.findDistinctContextByTimeRange(featureBucketConf, startTime, endTime);
    }

	public List<FeatureBucket> getFeatureBucketsByContextIdAndTimeRange(FeatureBucketConf featureBucketConf,
                                                                        String contextId,
                                                                        long startTimeInSeconds,
                                                                        long endTimeInSeconds,
                                                                        String fieldName,
                                                                        boolean fieldMustExist,
                                                                        List<String> additionalFieldsToInclude) {

        return featureBucketsMongoStore.getFeatureBucketsWithSpecificFieldProjectionByContextIdAndTimeRange(
                featureBucketConf,
                contextId,
                startTimeInSeconds,
                endTimeInSeconds,
                fieldName,
                fieldMustExist,
                additionalFieldsToInclude);
	}
}
