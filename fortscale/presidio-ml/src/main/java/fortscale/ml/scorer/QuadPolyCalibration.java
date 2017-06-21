package fortscale.ml.scorer;

import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
import fortscale.ml.scorer.config.QuadPolyCalibrationConf;

public class QuadPolyCalibration {
	private double a2;
	private double a1;
	private double sensitivity;
	private double largestPValue;
	private boolean isScoreForLargeValues;
	private boolean isScoreForSmallValues;

	public QuadPolyCalibration(
			double a2, double a1, double sensitivity,
			boolean isScoreForLargeValues, boolean isScoreForSmallValues) {

		this.a2 = a2;
		this.a1 = a1;
		this.sensitivity = sensitivity;
		this.largestPValue = calcQuadraticLeftIntersection(a2, a1, sensitivity);
		this.isScoreForLargeValues = isScoreForLargeValues;
		this.isScoreForSmallValues = isScoreForSmallValues;
	}

	public QuadPolyCalibration(QuadPolyCalibrationConf conf) {
		this(conf.getA2(), conf.getA1(), conf.getSensitivity(),
				conf.isScoreForLargeValues(), conf.isScoreForSmallValues());
	}

	public double calibrateScore(double modelScore) {
		double calibratedScore = 0;
		double p = Math.abs(modelScore) - ContinuousValuesModelScorerAlgorithm.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
		p = Math.min(p * sensitivity, 1);

		if (p < largestPValue && ((modelScore > 0 && isScoreForLargeValues) || (modelScore < 0 && isScoreForSmallValues))) {
			calibratedScore = Math.max(a2 * Math.pow(p, 2) - a1 * p + 1, 0);
			calibratedScore = Math.round(calibratedScore * 100);
		}

		return calibratedScore;
	}

	private static double calcQuadraticLeftIntersection(double a2, double a1, double sensitivity) {
		// Use the quadratic formula
		return (a1 * sensitivity - Math.sqrt(Math.pow(a1 * sensitivity, 2) - 4 * a2 * Math.pow(sensitivity, 2)))
				/ (2 * a2 * Math.pow(sensitivity, 2));
	}
}
