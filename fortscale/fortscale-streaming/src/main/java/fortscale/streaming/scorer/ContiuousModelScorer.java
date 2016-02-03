package fortscale.streaming.scorer;

import fortscale.ml.scorer.QuadPolyCalibration;
import org.apache.samza.config.Config;

public class ContiuousModelScorer extends ModelScorer{
	public static final String A1_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a1";
	public static final String A2_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a2";
	public static final String SENSITIVITY_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.sensitivity";
	public static final String IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.large.value";
	public static final String IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.small.value";

	public static final boolean DEFAULT_IS_SCORE_FOR_LARGE_VALUES = true;
	public static final boolean DEFAULT_IS_SCORE_FOR_SMALL_VALUES = true;
	public static final double DEFAULT_SENSITIVITY_VALUE = 1.0;

	private QuadPolyCalibration calibration;

	public ContiuousModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);

		double a1 = config.getDouble(String.format(A1_CONFIG_FORMAT, scorerName));
		double a2 = config.getDouble(String.format(A2_CONFIG_FORMAT, scorerName));
		double sensitivity = config.getDouble(String.format(SENSITIVITY_CONFIG_FORMAT, scorerName), DEFAULT_SENSITIVITY_VALUE);
		boolean isScoreForLargeValues = config.getBoolean(String.format(IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_LARGE_VALUES);
		boolean isScoreForSmallValues = config.getBoolean(String.format(IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_SMALL_VALUES);

		calibration = new QuadPolyCalibration(a2, a1, sensitivity, isScoreForLargeValues, isScoreForSmallValues);
	}

	@Override
	protected double calibrateScore(double score){
		return calibration.calibrateScore(score);
	}
}
