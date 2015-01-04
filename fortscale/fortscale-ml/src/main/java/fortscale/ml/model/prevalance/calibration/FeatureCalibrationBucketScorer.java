package fortscale.ml.model.prevalance.calibration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class FeatureCalibrationBucketScorer implements IFeatureCalibrationBucketScorer {

	private Map<String, Double> featureValueToScoreMap = new HashMap<>();
	
	private boolean isFirstBucket = false;
	
	private double score = 0;
	private String scoreFeatureValue;
	
	
	@Override
	public double getScore(){
		if(score < 1){
			return getBoostedScore(1);
		}
		return score;
	}
	
	@Override
	public double getBoostedScore(int numOfFeatureValues){
		return score == 0 ? 0 : Math.pow(score, 2) + 0.1 * Math.pow((numOfFeatureValues-1), 2);
	}
	
	private void updateMaxScore(){
		score = 0;
		Iterator<Entry<String, Double>> featureValueToCountIter = featureValueToScoreMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<String, Double> featureValueToCountEntry = featureValueToCountIter.next();
			if(score < featureValueToCountEntry.getValue()){
				score = featureValueToCountEntry.getValue();
				scoreFeatureValue = featureValueToCountEntry.getKey();
			}
		}
	}
	
	@Override
	public double updateFeatureValueCount(String featureValue, double featureCount){
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
		double ret = Math.log(count+0.3) / Math.log(10);
		
		return ret;
	}
	
	@Override
	public double removeFeatureValue(String featureValue){
		featureValueToScoreMap.remove(featureValue);
		if(featureValue.equals(scoreFeatureValue)){
			updateMaxScore();
		}
		
		return getScore();
	}	
	
	@Override
	public int size(){
		return featureValueToScoreMap.size();
	}

}
