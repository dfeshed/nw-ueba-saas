package fortscale.streaming.model.calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureCalibration{
	
	private static int MAX_NUM_OF_BUCKETS = 30;
	
	private ArrayList<Double> scoreBucketsAggr = null;
	private ArrayList<IFeatureCalibrationBucketScorer> bucketScorerList = null;
	private Map<Object, Double> featureValueToCountMap = new HashMap<>();
	private double addedValue = 0;
	private int minusIndex = 2;
	private double total = 0;
	private Double minCount = null;
	private Object featureValueWithMinCount = null;
	private Class<?> featureCalibrationBucketScorerClass = null;
	
	
	
	public FeatureCalibration(Class<?> featureCalibrationBucketScorerClass){
		this.featureCalibrationBucketScorerClass = featureCalibrationBucketScorerClass;
	}
	
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
		int bucketIndex = (int)getBucketIndex(count) - minusIndex;
		Double reducedCount = reduceCount(count);
		IFeatureCalibrationBucketScorer bucketScorer = bucketScorerList.get(bucketIndex);
		double prevScore = bucketScorer.getScore();
		double score = bucketScorer.updateFeatureValueCount(featureValue, reducedCount);
		Double diffCount = score - prevScore;

		if(prevCount != null){
			int prevBucketIndex = (int)getBucketIndex(prevCount) - minusIndex;
			if(prevBucketIndex != bucketIndex){
				bucketScorer = bucketScorerList.get(prevBucketIndex);
				prevScore = bucketScorer.getScore();
				score = bucketScorer.removeFeatureValue(featureValue);
				Double prevDiffCount = score - prevScore;
				diffCount += prevDiffCount;
				
				for(int i = prevBucketIndex; i < bucketIndex; i++){
					scoreBucketsAggr.set(i, scoreBucketsAggr.get(i) + prevDiffCount);
				}
			}
		}
		
		total += diffCount;
		
		for(int i = bucketIndex; i < scoreBucketsAggr.size(); i++){
			scoreBucketsAggr.set(i, scoreBucketsAggr.get(i) + diffCount);
		}
		
		
	}
		
	
	
	private IFeatureCalibrationBucketScorer createFeatureCalibrationBucketScorer() throws Exception{
		return (IFeatureCalibrationBucketScorer) featureCalibrationBucketScorerClass.newInstance();
	}
	
	private void reinit() throws Exception{
		addedValue = 0;
		minusIndex = 2;
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

		//Deciding the smallest bucket index and the value which need to be added such that no count will
		//fall in a smaller bucket.
		long minValueIndex = (long)(Math.ceil(Math.log(minCount) / Math.log(2)));
		long minValueStartBucket = Math.max(4,(int)Math.pow(2, minValueIndex));
		addedValue = minValueStartBucket - minCount;
		
		//Filling the buckets with all the feature values counts.
		//while doing so the counts are getting reduced in the method reduceCount
		initBucketScoreList();
		
		//Filling the an aggregated histogram of the buckets.
		scoreBucketsAggr = new ArrayList<>(MAX_NUM_OF_BUCKETS);
		double sum = 0;
		for(int i = 0; i < bucketScorerList.size(); i++){
			sum += bucketScorerList.get(i).getScore();
			scoreBucketsAggr.add(sum);
		}
		total = sum;
	}
	
	//Filling the buckets with all the feature values counts.
	//while doing so the counts are getting reduced in the method reduceCount
	private void initBucketScoreList() throws Exception{
		bucketScorerList = new ArrayList<>(MAX_NUM_OF_BUCKETS);
		for(int i = 0; i < MAX_NUM_OF_BUCKETS; i++){
			IFeatureCalibrationBucketScorer bucketScorer = createFeatureCalibrationBucketScorer();
			bucketScorer.setBucketIndex(i);
			bucketScorerList.add(bucketScorer);
		}
		
		int minIndex = MAX_NUM_OF_BUCKETS;
		Iterator<Entry<Object, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()){
			Entry<Object, Double> featureValueToCountEntry = featureValueToCountIter.next();
			int bucketIndex = (int)getBucketIndex(featureValueToCountEntry.getValue());
			minIndex = Math.min(minIndex, bucketIndex);
			Double reducedCount = reduceCount(featureValueToCountEntry.getValue());
			IFeatureCalibrationBucketScorer bucketScorer = bucketScorerList.get(bucketIndex);
			bucketScorer.updateFeatureValueCount(featureValueToCountEntry.getKey(), reducedCount);
		}
		
		minusIndex = minIndex;
		if(minusIndex > 0){
			for(int i = 0; i < minusIndex; i++){
				bucketScorerList.remove(0);
			}
			for(int i = 0; i < bucketScorerList.size(); i++){
				bucketScorerList.get(i).setBucketIndex(i);
			}
		}
	}

	
	public double score(Object featureValue) {
		if(scoreBucketsAggr == null){
			return 0;
		}
		
		Double featureCount = featureValueToCountMap.get(featureValue);
		if(featureCount == null){
			featureCount = 1D;
		}
		
		double bucketIndex = getBucketIndex(featureCount) - minusIndex;
		if(bucketIndex + 1 >= scoreBucketsAggr.size()){
			return 0;
		}
		
		int lowerBucketIndex = (int) bucketIndex;
		
		double ret = (scoreBucketsAggr.get(lowerBucketIndex) * (lowerBucketIndex + 1 - bucketIndex)) +
				(scoreBucketsAggr.get(lowerBucketIndex+1) * (bucketIndex - lowerBucketIndex));
		return 1 - (ret / total);
	}
	
	private double getBucketIndex(double rscore){
		return Math.min(Math.log(rscore + addedValue) / Math.log(2),MAX_NUM_OF_BUCKETS-1); 
	}
	
	private double reduceCount(double count){
		double ret = Math.log(count+1) / Math.log(2);
		ret = Math.pow(ret, 2);
		
		return ret;
	}

}
