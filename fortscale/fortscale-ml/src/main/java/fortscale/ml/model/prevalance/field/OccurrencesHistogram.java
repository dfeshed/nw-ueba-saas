package fortscale.ml.model.prevalance.field;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.prevalance.calibration.FeatureCalibrationBucketScorer;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class OccurrencesHistogram {
	private final static int NUM_OF_BUCKETS = 30;
	private final static double ADDED_VALUE = 1;

	private ArrayList<FeatureCalibrationBucketScorer> bucketScorerList;
	private double maxBucketScore;

	public OccurrencesHistogram(Map<String, Double> featureValueToCountMap) {
		bucketScorerList = new ArrayList<>(NUM_OF_BUCKETS);
		for (int i = 0; i < NUM_OF_BUCKETS; i++) {
			bucketScorerList.add(new FeatureCalibrationBucketScorer());
		}
		updateBucketScorerList(featureValueToCountMap);
		calcMaxBucketScore();
	}

	private void updateBucketScorerList(Map<String, Double> featureValueToCountMap) {
		for (Entry<String, Double> featureValueToCount : featureValueToCountMap.entrySet()) {
			Double count = featureValueToCount.getValue();
			if (count >= 1) {
				String featureValue = featureValueToCount.getKey();
				int bucketIndex = (int) getBucketIndex(count);
				bucketScorerList.get(bucketIndex).updateFeatureValueCount(featureValue, count);
			}
		}
	}

	private void calcMaxBucketScore() {
		maxBucketScore = 0;
		for (int i = 0; i < bucketScorerList.size(); i++) {
			maxBucketScore = Math.max(maxBucketScore, bucketScorerList.get(i).getScore());
		}
	}

	public double score(Double featureCount) {
		if (maxBucketScore == 0) {
			return 0;
		}

		if (featureCount == null) {
			featureCount = 1D;
		}

		double bucketIndex = getBucketIndex(featureCount);
		if (bucketIndex + 1 >= bucketScorerList.size()) {
			return 0;
		}

		int lowerBucketIndex = (int) bucketIndex;
		double lowerBucketScore = 0;
		int size = 0;
		for (int i = 0; i <= lowerBucketIndex; i++) {
			size += bucketScorerList.get(i).size();
			lowerBucketScore = Math.max(lowerBucketScore, bucketScorerList.get(i).getBoostedScore(size));
		}

		size += bucketScorerList.get(lowerBucketIndex + 1).size();
		double upperBucketScore = Math.max(lowerBucketScore, bucketScorerList.get(lowerBucketIndex+1).getBoostedScore(size));
		// smoothing the score between element in bucketIndex and with the elements in the next bucket.
		// nextBucketMinInfluence means that elements in bucketIndex will be influenced by the next bucket
		// by at least this factor.
		double nextBucketMinInfluence =  0.4 / (Math.pow(2, lowerBucketIndex + 1) - Math.pow(2, lowerBucketIndex));
		double lowerBucketInfluence = lowerBucketIndex + 1 - bucketIndex - nextBucketMinInfluence;
		double ret = (lowerBucketScore * lowerBucketInfluence) + (upperBucketScore * (1 - lowerBucketInfluence));

		return ret > maxBucketScore ? 0 : (int) ((1 - (ret / maxBucketScore)) * 100);
	}

	private double getBucketIndex(double featureCount) {
		double num = featureCount + ADDED_VALUE;
		return num < 2 ? 0 : Math.min((Math.log(num) / Math.log(2)) - 1, NUM_OF_BUCKETS - 1);
	}
}
