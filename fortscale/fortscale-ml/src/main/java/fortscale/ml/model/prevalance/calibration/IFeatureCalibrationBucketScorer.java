package fortscale.ml.model.prevalance.calibration;

public interface IFeatureCalibrationBucketScorer {
	public double getScore();
	public double updateFeatureValueCount(String featureValue, double featureCount);
	public double removeFeatureValue(String featureValue);
	public int size();
	double getBoostedScore(int numOfFeatureValues);
}
