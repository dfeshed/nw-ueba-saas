package fortscale.streaming.model.calibration;

public interface IFeatureCalibrationBucketScorer {
	public double getScore();
	public double updateFeatureValueCount(Object featureValue, double featureCount);
	public double removeFeatureValue(Object featureValue);
	public boolean getIsFirstBucket();
	public void setIsFirstBucket(boolean isFirstBucket);
}
