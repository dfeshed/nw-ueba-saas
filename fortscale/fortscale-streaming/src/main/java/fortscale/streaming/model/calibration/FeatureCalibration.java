package fortscale.streaming.model.calibration;

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
	
	private Double scoreBucketsAggr[];
	private ArrayList<FeatureCalibrationBucketScorer> bucketScorerList = null;
	private Map<Object, Double> featureValueToCountMap = new HashMap<>();
	private double addedValue = 1;
	private double total = 0;
	private Double minCount = null;
	private Object featureValueWithMinCount = null;
	
		
	
	
	public Double getFeatureValueCount(Object featureValue){
		return featureValueToCountMap.get(featureValue);
	}
	
	//Filling the feature values to count map with the given map
	//The values in the map expected to be above 1. Hence values below 1 are not added.
	public void init(Map<Object, Double> featureValueToCountMap) throws Exception {
		if(featureValueToCountMap.size() == 0){
			return;
		}
		
		
		this.featureValueToCountMap = new HashMap<>();
		Iterator<Entry<Object, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<Object, Double> featureValueToCountEntry = featureValueToCountIter.next();
			if(featureValueToCountEntry.getValue() < 1){
				continue;
			}
			this.featureValueToCountMap.put(featureValueToCountEntry.getKey(), featureValueToCountEntry.getValue());
		}
		
		reinit();
	}
	
	public void incrementFeatureValue(Object featureValue) throws Exception{
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
	public void updateFeatureValueCount(Object featureValue, Double count) throws Exception{
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
			isReinit = true;
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
				bucketScorer = bucketScorerList.get(prevBucketIndex);
				bucketScorer.removeFeatureValue(featureValue);
			}
		}
				
		fillScoreBucketAggr();
		
	}
		
	
	
	private FeatureCalibrationBucketScorer createFeatureCalibrationBucketScorer() throws Exception{
		return new FeatureCalibrationBucketScorer();
	}
	
	private void reinit() throws Exception{
		total = 0;
		minCount = null;
		featureValueWithMinCount = null;
		//Finding the smaller count of all the feature values
		Iterator<Entry<Object, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<Object, Double> featureValueToCountEntry = featureValueToCountIter.next();
			Double val = featureValueToCountEntry.getValue();
			if(minCount == null || val < minCount){
				minCount = val;
				featureValueWithMinCount = featureValueToCountEntry.getKey();
			}
		}

		
		//Filling the buckets with all the feature values counts.
		//while doing so the counts are getting reduced in the method reduceCount
		initBucketScoreList();
		
		//Filling the an aggregated histogram of the buckets.
		scoreBucketsAggr = new Double[MAX_NUM_OF_BUCKETS];
		fillScoreBucketAggr();
	}
	
	private void fillScoreBucketAggr(){
		double sum = 0;
		for(int i = 0; i < bucketScorerList.size(); i++){
			sum += bucketScorerList.get(i).getScore();
			scoreBucketsAggr[i] = sum;
		}
		total = sum;
	}
	
	//Filling the buckets with all the feature values counts.
	//while doing so the counts are getting reduced in the method reduceCount
	private void initBucketScoreList() throws Exception{
		bucketScorerList = new ArrayList<>(MAX_NUM_OF_BUCKETS);
		for(int i = 0; i < MAX_NUM_OF_BUCKETS; i++){
			FeatureCalibrationBucketScorer bucketScorer = createFeatureCalibrationBucketScorer();
			bucketScorerList.add(bucketScorer);
		}
		
		int minIndex = MAX_NUM_OF_BUCKETS;
		Iterator<Entry<Object, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<Object, Double> featureValueToCountEntry = featureValueToCountIter.next();
			int bucketIndex = (int)getBucketIndex(featureValueToCountEntry.getValue());
			minIndex = Math.min(minIndex, bucketIndex);
			FeatureCalibrationBucketScorer bucketScorer = bucketScorerList.get(bucketIndex);
			bucketScorer.updateFeatureValueCount(featureValueToCountEntry.getKey(), featureValueToCountEntry.getValue());
		}
		bucketScorerList.get(minIndex).setIsFirstBucket(true);		
	}

	
	public double score(Object featureValue) {
		if(scoreBucketsAggr == null){
			return 0;
		}
		
		Double featureCount = featureValueToCountMap.get(featureValue);
		if(featureCount == null){
			featureCount = 1D;
		}
		
		double bucketIndex = getBucketIndex(featureCount);
		if(bucketIndex + 1 >= scoreBucketsAggr.length){
			return 0;
		}
		
		int lowerBucketIndex = (int) bucketIndex;
		double ret = scoreBucketsAggr[lowerBucketIndex];
		if(lowerBucketIndex != bucketIndex){
			ret = (scoreBucketsAggr[lowerBucketIndex] * (lowerBucketIndex + 1 - bucketIndex)) +
					(scoreBucketsAggr[lowerBucketIndex+1] * (bucketIndex - lowerBucketIndex));
		}
		return 1 - (ret / total);
	}
	
	private double getBucketIndex(double rscore){
		double num = rscore + addedValue;
		return num < 2 ? 0 : Math.min((Math.log(num) / Math.log(2)) - 1,MAX_NUM_OF_BUCKETS-1); 
	}

}
