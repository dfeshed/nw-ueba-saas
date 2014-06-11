package fortscale.streaming.model.calibration;

import java.util.HashMap;
import java.util.Map;

public class FeatureCalibrationBucketScorer implements IFeatureCalibrationBucketScorer{

	private Map<Object, Double> featureValueToScoreMap = new HashMap<>();
	
	private int bucketIndex = 0;
	
	private double score = 0;
	
	@Override
	public double getScore(){
		return score;
	}
	
	@Override
	public double updateFeatureValueCount(Object featureValue, double featureScore){
		Double prevFeatureScore = featureValueToScoreMap.get(featureValue);
		featureValueToScoreMap.put(featureValue, featureScore);
		if(prevFeatureScore == null){
			score += featureScore;
		} else{
			score += (featureScore - prevFeatureScore);
		}
		
		return score;
	}
	
	@Override
	public double removeFeatureValue(Object featureValue){
		Double featureScore = featureValueToScoreMap.get(featureValue);
		if(featureScore != null){
			featureValueToScoreMap.remove(featureValue);
			score = score - featureScore;
		}
		
		return score;
	}

	public int getBucketIndex() {
		return bucketIndex;
	}

	public void setBucketIndex(int bucketIndex) {
		this.bucketIndex = bucketIndex;
	}
}
