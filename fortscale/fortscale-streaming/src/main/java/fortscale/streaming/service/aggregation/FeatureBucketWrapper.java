package fortscale.streaming.service.aggregation;

public class FeatureBucketWrapper {
	private FeatureBucket featureBucket;

	public FeatureBucketWrapper(FeatureBucket featureBucket) {
		this.featureBucket = featureBucket;
	}

	public FeatureBucket getFeatureBucket() {
		return featureBucket;
	}

	public void setFeatureBucket(FeatureBucket featureBucket) {
		this.featureBucket = featureBucket;
	}
}
