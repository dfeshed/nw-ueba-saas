package fortscale.streaming.model.calibration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TimeFeatureCalibrationBucketScorer implements
		IFeatureCalibrationBucketScorer {

private Map<Object, Double> featureValueToScoreMap = new HashMap<>();
	
	private int bucketIndex;
	
	private double score = 0;
	private Object scoreFeatureValue;
	
	
	@Override
	public double getScore(){
		if(bucketIndex == 0){
			return score * Math.pow(4, featureValueToScoreMap.size()/3);
		} else{
			return score;
		}
	}
	
	private void updateMaxScore(){
		score = 0;
		Iterator<Entry<Object, Double>> featureValueToCountIter = featureValueToScoreMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<Object, Double> featureValueToCountEntry = featureValueToCountIter.next();
			if(score < featureValueToCountEntry.getValue()){
				score = featureValueToCountEntry.getValue();
				scoreFeatureValue = featureValueToCountEntry.getKey();
			}
		}
	}
	
	@Override
	public double updateFeatureValueCount(Object featureValue, double featureScore){
		featureValueToScoreMap.put(featureValue, featureScore);
		if(scoreFeatureValue == null){
			score = featureScore;
			this.scoreFeatureValue = featureValue;
		} else{
			if(scoreFeatureValue.equals(featureValue)){
				if(featureScore >= score){
					score = featureScore;
				} else{
					updateMaxScore();
				}
			} else if(featureScore > score){
				score = featureScore;
				scoreFeatureValue = featureValue;
			}
		}
		
		return getScore();
	}
	
	@Override
	public double removeFeatureValue(Object featureValue){
		featureValueToScoreMap.remove(featureValue);
		if(featureValue.equals(scoreFeatureValue)){
			updateMaxScore();
		}
		
		return getScore();
	}

	@Override
	public int getBucketIndex() {
		return bucketIndex;
	}

	@Override
	public void setBucketIndex(int bucketIndex) {
		this.bucketIndex = bucketIndex;
	}

}
