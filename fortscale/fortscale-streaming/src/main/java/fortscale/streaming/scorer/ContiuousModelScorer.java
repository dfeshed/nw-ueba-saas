package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.field.QuadPolyCalibrationForContModel;

public class ContiuousModelScorer extends ModelScorer{
	
	public static final String A1_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a1";
	public static final String A2_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a2";
	public static final String LARGEST_PVALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.largest.p.value";
	public static final String IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.large.value";
	public static final String IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.small.value";
	
	public static final boolean DEFAULT_IS_SCORE_FOR_LARGE_VALUES = true;
	public static final boolean DEFAULT_IS_SCORE_FOR_SMALL_VALUES = true;
	
	private QuadPolyCalibrationForContModel calibration;
	

	public ContiuousModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		
		double a1 = config.getDouble(String.format(A1_CONFIG_FORMAT, scorerName));
		double a2 = config.getDouble(String.format(A2_CONFIG_FORMAT, scorerName));
		double largestPValue = config.getDouble(String.format(LARGEST_PVALUE_CONFIG_FORMAT, scorerName));
		boolean isScoreForLargeValues = config.getBoolean(String.format(IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_LARGE_VALUES);
		boolean isScoreForSmallValues = config.getBoolean(String.format(IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_SMALL_VALUES);
		
		calibration = new QuadPolyCalibrationForContModel(a2, a1, largestPValue, isScoreForLargeValues, isScoreForSmallValues);
	}

	@Override
	protected double calibrateScore(double score){
		 return calibration.calculateScore(score);
	}
}
