package fortscale.aggregation.feature.bucket;

public class FeatureBucketWrapper {
	private FeatureBucket featureBucket;
	private boolean isNew = false;

	public FeatureBucketWrapper(FeatureBucket featureBucket, boolean isNew) {
		this.featureBucket = featureBucket;
		this.isNew = isNew;
	}

	public FeatureBucket getFeatureBucket() {
		return featureBucket;
	}

	public void setFeatureBucket(FeatureBucket featureBucket) {
		this.featureBucket = featureBucket;
	}

	public boolean isNew() {
		return isNew;
	}
	
	
}
