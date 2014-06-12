package fortscale.streaming.model.calibration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureCalibrationBucketScorer implements
		IFeatureCalibrationBucketScorer {

private Map<Object, Double> featureValueToScoreMap = new HashMap<>();
	
	private boolean isFirstBucket = false;
	
	private double score = 0;
	private Object scoreFeatureValue;
	
	
	@Override
	public double getScore(){
		if(isFirstBucket){
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
	public double updateFeatureValueCount(Object featureValue, double featureCount){
		double featureScore = reduceCount(featureCount);
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
	
	private double reduceCount(double count){
		double ret = Math.log(count+1) / Math.log(2);
		ret = Math.pow(ret, 2);
		
		return ret;
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
	public boolean getIsFirstBucket() {
		return isFirstBucket;
	}

	@Override
	public void setIsFirstBucket(boolean isFirstBucket) {
		this.isFirstBucket = isFirstBucket;
	}

}
