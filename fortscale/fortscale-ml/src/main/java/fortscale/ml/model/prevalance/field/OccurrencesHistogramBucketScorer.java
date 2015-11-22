package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class OccurrencesHistogramBucketScorer {
	private double score;
	private int numOfFeaturesInBucket;

	public OccurrencesHistogramBucketScorer() {
		score = 0;
		numOfFeaturesInBucket = 0;
	}

	public double getScore() {
		if (score < 1) {
			return getBoostedScore(1);
		}
		return score;
	}

	public double getBoostedScore(int numOfFeatureValues) {
		return score == 0 ? 0 : Math.pow(score, 2) + 0.1 * Math.pow((numOfFeatureValues-1), 2);
	}

	public void addFeatureCount(double featureCount) {
		double featureScore = reduceCount(featureCount);
		score = Math.max(score, featureScore);
		numOfFeaturesInBucket++;
	}
	
	private double reduceCount(double count) {
		return Math.log(count+0.3) / Math.log(10);
	}

	public int getNumOfFeaturesInBucket() {
		return numOfFeaturesInBucket;
	}
}
