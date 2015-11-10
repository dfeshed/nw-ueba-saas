package fortscale.aggregation.feature.bucket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class FeatureBucketsReaderService {

	@Autowired
	private FeatureBucketsMongoStore featureBucketsMongoStore;
	
	public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(FeatureBucketConf featureBucketConf, String contextType, String ContextName, Long bucketStartTime, Long bucketEndTime) {
		return featureBucketsMongoStore.getFeatureBucketsByContextAndTimeRange(featureBucketConf, contextType, ContextName, bucketStartTime, bucketEndTime);
	}
	
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String bucketId){
		return featureBucketsMongoStore.getFeatureBucket(featureBucketConf, bucketId);
	}
	
	public List<String> findDistinctContextByTimeRange(FeatureBucketConf featureBucketConf, Long startTime, Long endTime){
		return featureBucketsMongoStore.findDistinctContextByTimeRange(featureBucketConf, startTime, endTime);
	}

	public List<FeatureBucket> getFeatureBucketsByContextIdAndTimeRange(FeatureBucketConf featureBucketConf, String contextId, long startTimeInSeconds, long endTimeInSeconds) {
		return featureBucketsMongoStore.getFeatureBucketsByContextIdAndTimeRange(featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds);
	}
}
