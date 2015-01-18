package fortscale.ml.model.prevalance.calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class FeatureCalibration{
	
	private static int MAX_NUM_OF_BUCKETS = 30;
	
	private ArrayList<FeatureCalibrationBucketScorer> bucketScorerList = null;
	private Map<String, Double> featureValueToCountMap = new HashMap<>();
	private double addedValue = 1;
	private double total = 0;
	private Double minCount = null;
	private String featureValueWithMinCount = null;
	
		
	
	
	public Double getFeatureValueCount(String featureValue){
		return featureValueToCountMap.get(featureValue);
	}
	
	//Filling the feature values to count map with the given map
	//The values in the map expected to be above 1. Hence values below 1 are not added.
	public void init(Map<String, Double> featureValueToCountMap) throws Exception {
		if(featureValueToCountMap.size() == 0){
			return;
		}
		
		
		this.featureValueToCountMap = new HashMap<>();
		Iterator<Entry<String, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<String, Double> featureValueToCountEntry = featureValueToCountIter.next();
			if(featureValueToCountEntry.getValue() < 1){
				continue;
			}
			this.featureValueToCountMap.put(featureValueToCountEntry.getKey(), featureValueToCountEntry.getValue());
		}
		
		reinit();
	}
	
	public void incrementFeatureValue(String featureValue) throws Exception{
		Double count = featureValueToCountMap.get(featureValue);
		if(count == null){
			count = 1D;
		} else{
			count++;
		}
		
		updateFeatureValueCount(featureValue, count);
	}
	
	//updating feature value with a new count.
	//the value is expected to be >= 1
	public void updateFeatureValueCount(String featureValue, Double count) throws Exception{
		if(count < 1){
			return;
		}
				
		//checking whether it is possible to just do an update or there is a need to reinit the calibration.
		Double prevCount = featureValueToCountMap.get(featureValue);
		featureValueToCountMap.put(featureValue, count);
		boolean isReinit = false;
		if(prevCount == null){
			if(minCount == null || count < minCount){
				isReinit = true;
			}
		} else if(featureValue.equals(featureValueWithMinCount)){
			recalculateFeatureValueWithMinCount();
		}
		
		if(isReinit){
			reinit();
			//the calibration was reinitialized with the new value.
			return;
		}

		//There was no need to reinit the calibration for this update.
		int bucketIndex = (int)getBucketIndex(count);
		FeatureCalibrationBucketScorer bucketScorer = bucketScorerList.get(bucketIndex);
		bucketScorer.updateFeatureValueCount(featureValue, count);

		if(prevCount != null){
			int prevBucketIndex = (int)getBucketIndex(prevCount);
			if(prevBucketIndex != bucketIndex){
				FeatureCalibrationBucketScorer prevBucketScorer = bucketScorerList.get(prevBucketIndex);
				prevBucketScorer.removeFeatureValue(featureValue);
			}
		}
				
		setMaxScore();
		
	}
		
	
	
	private FeatureCalibrationBucketScorer createFeatureCalibrationBucketScorer() throws Exception{
		return new FeatureCalibrationBucketScorer();
	}
	
	private void reinit() throws Exception{
		total = 0;
		
		//Finding the smaller count of all the feature values
		recalculateFeatureValueWithMinCount();
		
		//Filling the buckets with all the feature values counts.
		//while doing so the counts are getting reduced in the method reduceCount
		initBucketScoreList();
		
		setMaxScore();
	}
	
	//Finding the smaller count of all the feature values
	private void recalculateFeatureValueWithMinCount(){
		minCount = null;
		featureValueWithMinCount = null;
		Iterator<Entry<String, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<String, Double> featureValueToCountEntry = featureValueToCountIter.next();
			Double val = featureValueToCountEntry.getValue();
			if(minCount == null || val < minCount){
				minCount = val;
				featureValueWithMinCount = featureValueToCountEntry.getKey();
			}
		}
	}
	
	private void setMaxScore(){
		total = 0;
		for(int i = 0; i < bucketScorerList.size(); i++){
			total = Math.max(total, bucketScorerList.get(i).getScore());
		}
	}
	
	//Filling the buckets with all the feature values counts.
	//while doing so the counts are getting reduced in the method reduceCount
	private void initBucketScoreList() throws Exception{
		bucketScorerList = new ArrayList<>(MAX_NUM_OF_BUCKETS);
		for(int i = 0; i < MAX_NUM_OF_BUCKETS; i++){
			FeatureCalibrationBucketScorer bucketScorer = createFeatureCalibrationBucketScorer();
			bucketScorerList.add(bucketScorer);
		}
		
		Iterator<Entry<String, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<String, Double> featureValueToCountEntry = featureValueToCountIter.next();
			int bucketIndex = (int)getBucketIndex(featureValueToCountEntry.getValue());
			FeatureCalibrationBucketScorer bucketScorer = bucketScorerList.get(bucketIndex);
			bucketScorer.updateFeatureValueCount(featureValueToCountEntry.getKey(), featureValueToCountEntry.getValue());
		}
	}

	
	public double score(String featureValue) {
		if(total == 0){
			return 0;
		}
		
		Double featureCount = featureValueToCountMap.get(featureValue);
		if(featureCount == null){
			featureCount = 1D;
		}
		
		double bucketIndex = getBucketIndex(featureCount);
		if(bucketIndex + 1 >= bucketScorerList.size()){
			return 0;
		}
		
		int lowerBucketIndex = (int) bucketIndex;
		double lowerBucketScore = 0;
		int size = 0;
		for(int i = 0; i <= lowerBucketIndex; i++){
			size += bucketScorerList.get(i).size();
			lowerBucketScore = Math.max(lowerBucketScore, bucketScorerList.get(i).getBoostedScore(size));
		}
		
		double upperBucketScore = Math.max(lowerBucketScore, bucketScorerList.get(lowerBucketIndex+1).getBoostedScore(size + bucketScorerList.get(lowerBucketIndex+1).size()));
		// smoothing the score between element in bucketIndex and with the elements in the next bucket.
		// The 0.2 means that elements in bucketIndex will be influenced by the next bucket by at least 0.2
		double nextBucketMinInfluence =  0.4/(Math.pow(2, lowerBucketIndex+1) - Math.pow(2, lowerBucketIndex));
		double ret = (lowerBucketScore * (lowerBucketIndex + 1 - bucketIndex - nextBucketMinInfluence)) +
				(upperBucketScore * (bucketIndex - lowerBucketIndex + nextBucketMinInfluence));
		
		return ret > total ? 0 :(int) ((1 - (ret / total))*100);
	}
	
	
	
	private double getBucketIndex(double rscore){
		double num = rscore + addedValue;
		return num < 2 ? 0 : Math.min((Math.log(num) / Math.log(2)) - 1,MAX_NUM_OF_BUCKETS-1); 
	}

}
