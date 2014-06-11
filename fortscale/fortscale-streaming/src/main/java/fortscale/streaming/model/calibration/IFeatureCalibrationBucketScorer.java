package fortscale.streaming.model.calibration;

public interface IFeatureCalibrationBucketScorer {
	public double getScore();
	public double updateFeatureValueCount(Object featureValue, double featureScore);
	public double removeFeatureValue(Object featureValue);
	public int getBucketIndex();
	public void setBucketIndex(int bucketIndex);
}
