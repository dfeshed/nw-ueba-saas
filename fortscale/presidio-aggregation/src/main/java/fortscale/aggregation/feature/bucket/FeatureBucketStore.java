package fortscale.aggregation.feature.bucket;

public interface FeatureBucketStore extends FeatureBucketReader {
	void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket);
}
