package fortscale.streaming.model.calibration;

public interface IFeatureCalibrationBucketScorer {
	public double getScore();
	public double updateFeatureValueCount(String featureValue, double featureCount);
	public double removeFeatureValue(String featureValue);
	public boolean getIsFirstBucket();
	public void setIsFirstBucket(boolean isFirstBucket);
	public int size();
}
