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

	private List<OccurrencesHistogramBucketScorer> bucketScorers;
	private double maxBucketScore;

	public OccurrencesHistogram(List<Double> featureOccurrences) {
		bucketScorers = new ArrayList<>(NUM_OF_BUCKETS);
		for (int i = 0; i < NUM_OF_BUCKETS; i++) {
			bucketScorers.add(new OccurrencesHistogramBucketScorer());
		}
		updateBucketScorerList(featureOccurrences);
		calcMaxBucketScore();
	}

	private void updateBucketScorerList(List<Double> featureOccurrences) {
		for (Double occurrence : featureOccurrences) {
			if (occurrence >= 1) {
				bucketScorers.get((int) getBucketIndex(occurrence)).addFeatureCount(occurrence);
			}
		}
	}

	private void calcMaxBucketScore() {
		maxBucketScore = 0;
		for (int i = 0; i < bucketScorers.size(); i++) {
			maxBucketScore = Math.max(maxBucketScore, bucketScorers.get(i).getScore());
		}
	}

	public double score(Double featureCount) {
		if (maxBucketScore == 0) {
			return 0;
		}

		double bucketIndex = getBucketIndex(featureCount);
		if (bucketIndex + 1 >= bucketScorers.size()) {
			return 0;
		}

		int lowerBucketIndex = (int) bucketIndex;
		double lowerBucketScore = 0;
		int aggregatedNumOfFeatures = 0;
		for (int i = 0; i <= lowerBucketIndex; i++) {
			aggregatedNumOfFeatures += bucketScorers.get(i).getNumOfFeaturesInBucket();
			lowerBucketScore = Math.max(lowerBucketScore, bucketScorers.get(i).getBoostedScore(aggregatedNumOfFeatures));
		}

		aggregatedNumOfFeatures += bucketScorers.get(lowerBucketIndex + 1).getNumOfFeaturesInBucket();
		double upperBucketScore = Math.max(lowerBucketScore, bucketScorers.get(lowerBucketIndex + 1).getBoostedScore(aggregatedNumOfFeatures));
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
