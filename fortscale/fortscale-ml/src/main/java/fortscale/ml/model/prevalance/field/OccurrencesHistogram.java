package fortscale.ml.model.prevalance.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.prevalance.calibration.FeatureCalibrationBucketScorer;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class OccurrencesHistogram {

	private final static int MAX_NUM_OF_BUCKETS = 30;
	private final static double ADDED_VALUE = 1;

	private ArrayList<FeatureCalibrationBucketScorer> bucketScorerList;
	private Map<String, Double> featureValueToCountMap;
	private double total;

	public OccurrencesHistogram(Map<String, Double> featureValueToCountMap) {
		if(featureValueToCountMap.size() == 0){
			return;
		}

		this.featureValueToCountMap = new HashMap<>();
		Iterator<Entry<String, Double>> featureValueToCountIter = featureValueToCountMap.entrySet().iterator();
		while(featureValueToCountIter.hasNext()) {
			Entry<String, Double> featureValueToCountEntry = featureValueToCountIter.next();
			if (featureValueToCountEntry.getValue() < 1) {
				continue;
			}
			this.featureValueToCountMap.put(featureValueToCountEntry.getKey(), featureValueToCountEntry.getValue());
		}

		//Filling the buckets with all the feature values counts.
		//while doing so the counts are getting reduced in the method reduceCount
		initBucketScoreList();

		total = 0;
		for(int i = 0; i < bucketScorerList.size(); i++){
			total = Math.max(total, bucketScorerList.get(i).getScore());
		}
	}

	//Filling the buckets with all the feature values counts.
	//while doing so the counts are getting reduced in the method reduceCount
	private void initBucketScoreList(){
		bucketScorerList = new ArrayList<>(MAX_NUM_OF_BUCKETS);
		for(int i = 0; i < MAX_NUM_OF_BUCKETS; i++){
			FeatureCalibrationBucketScorer bucketScorer =new FeatureCalibrationBucketScorer();
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


	//TODO: delete score function
	public double score(String featureValue) {
		return scoreFeatureCount(featureValueToCountMap.get(featureValue));
	}


	public double scoreFeatureCount(Double featureCount) {
		if(total == 0){
			return 0;
		}

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
		double num = rscore + ADDED_VALUE;
		return num < 2 ? 0 : Math.min((Math.log(num) / Math.log(2)) - 1,MAX_NUM_OF_BUCKETS-1);
	}

}
