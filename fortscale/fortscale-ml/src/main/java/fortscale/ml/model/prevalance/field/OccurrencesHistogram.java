package fortscale.ml.model.prevalance.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class OccurrencesHistogram {
	private final static int NUM_OF_BUCKETS = 30;
	private final static double ADDED_VALUE = 1;

	private List<OccurrencesHistogramBucket> buckets;
	private double maxBucketScore;

	public OccurrencesHistogram(Map<String, Double> featureValueToCountMap) {
		buckets = new ArrayList<>(NUM_OF_BUCKETS);
		for (int i = 0; i < NUM_OF_BUCKETS; i++) {
			buckets.add(new OccurrencesHistogramBucket());
		}
		updateBucketScorerList(featureValueToCountMap);
		calcMaxBucketScore();
	}

	private void updateBucketScorerList(Map<String, Double> featureValueToCountMap) {
		for (Double count : featureValueToCountMap.values()) {
			if (count >= 1) {
				buckets.get((int) getBucketIndex(count)).addFeatureCount(count);
			}
		}
	}

	private void calcMaxBucketScore() {
		maxBucketScore = 0;
		for (int i = 0; i < buckets.size(); i++) {
			maxBucketScore = Math.max(maxBucketScore, buckets.get(i).getScore());
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
		if (bucketIndex + 1 >= buckets.size()) {
			return 0;
		}

		int lowerBucketIndex = (int) bucketIndex;
		double lowerBucketScore = 0;
		int aggregatedNumOfFeatures = 0;
		for (int i = 0; i <= lowerBucketIndex; i++) {
			aggregatedNumOfFeatures += buckets.get(i).getNumOfFeaturesInBucket();
			lowerBucketScore = Math.max(lowerBucketScore, buckets.get(i).getBoostedScore(aggregatedNumOfFeatures));
		}

		aggregatedNumOfFeatures += buckets.get(lowerBucketIndex + 1).getNumOfFeaturesInBucket();
		double upperBucketScore = Math.max(lowerBucketScore, buckets.get(lowerBucketIndex + 1).getBoostedScore(aggregatedNumOfFeatures));
		// smoothing the score between element in bucketIndex and with the elements in the next bucket.
		// nextBucketMinInfluence means that elements in bucketIndex will be influenced by the next bucket
		// by at least this factor.
		double nextBucketMinInfluence =  0.4 / Math.pow(2, lowerBucketIndex);
		double lowerBucketInfluence = lowerBucketIndex + 1 - bucketIndex - nextBucketMinInfluence;
		double ret = (lowerBucketScore * lowerBucketInfluence) + (upperBucketScore * (1 - lowerBucketInfluence));

		return ret > maxBucketScore ? 0 : (int) ((1 - (ret / maxBucketScore)) * 100);
	}

	private double getBucketIndex(double featureCount) {
		double num = featureCount + ADDED_VALUE;
		return num < 2 ? 0 : Math.min((Math.log(num) / Math.log(2)) - 1, NUM_OF_BUCKETS - 1);
	}
}
