package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class SMARTThresholdModel implements Model {
	public static final int SMALLEST_HIGH_SCORE = 50;
	private double threshold;
	private double maxSeenScore;

	public void init(double threshold, double maxSeenScore) {
		this.threshold = threshold;
		this.maxSeenScore = maxSeenScore;
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}

	public double transformScore(double score) {
		if (score >= threshold) {
			return Math.min(100, SMALLEST_HIGH_SCORE * (score + maxSeenScore - 2 * threshold) / (maxSeenScore - threshold));
		} else {
			return SMALLEST_HIGH_SCORE * score / threshold;
		}
	}

	public double restoreOriginalScore(double transformedScore) {
		if (transformedScore >= SMALLEST_HIGH_SCORE) {
			return (threshold * (2 * SMALLEST_HIGH_SCORE - transformedScore) +
					maxSeenScore * (transformedScore - SMALLEST_HIGH_SCORE)) / SMALLEST_HIGH_SCORE;
		} else {
			return transformedScore * threshold / SMALLEST_HIGH_SCORE;
		}
	}
}
