package fortscale.aggregation.feature.bucket;

public class BucketAlreadyExistException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private FeatureBucketConf existingBucketConf;
	private FeatureBucketConf newBucketConf;

	public BucketAlreadyExistException(FeatureBucketConf existingBucketConf, FeatureBucketConf newBucketConf){
		super(String.format("Bucket configuration already exist: existing bucket: %s, new bucket: %s", existingBucketConf.toString(), newBucketConf.toString()));
		this.existingBucketConf = existingBucketConf;
		this.newBucketConf = newBucketConf;
	}

	public FeatureBucketConf getExistingBucketConf() {
		return existingBucketConf;
	}

	public FeatureBucketConf getNewBucketConf() {
		return newBucketConf;
	}
}
