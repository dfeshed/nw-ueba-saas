package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.field.ContinuousValuesModel;

public class ContiuousModelScorer extends ModelScorer{
	
	public static final String A1_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a1";
	public static final String A2_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.a2";
	public static final String LARGEST_PVALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.largest.p.value";
	public static final String IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.large.value";
	public static final String IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.small.value";
	
	public static final boolean DEFAULT_IS_SCORE_FOR_LARGE_VALUES = true;
	public static final boolean DEFAULT_IS_SCORE_FOR_SMALL_VALUES = true;
	
	private double a2;
	private double a1;
	private double largestPValue;
	private boolean isScoreForLargeValues;
	private boolean isScoreForSmallValues;

	public ContiuousModelScorer(String scorerName, Config config, ScorerContext context) {
		super(scorerName, config, context);
		a1 = config.getDouble(String.format(A1_CONFIG_FORMAT, scorerName));
		a2 = config.getDouble(String.format(A2_CONFIG_FORMAT, scorerName));
		largestPValue = config.getDouble(String.format(LARGEST_PVALUE_CONFIG_FORMAT, scorerName));
		isScoreForLargeValues = config.getBoolean(String.format(IS_SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_LARGE_VALUES);
		isScoreForSmallValues = config.getBoolean(String.format(IS_SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, scorerName), DEFAULT_IS_SCORE_FOR_SMALL_VALUES);
	}

	@Override
	protected FeatureScore calculateModelScore(EventMessage eventMessage, PrevalanceModel model) throws Exception{
		double score = 0;
		if(model != null){
			double val = model.calculateScore(eventMessage.getJsonObject(), featureFieldName);
			double p = Math.abs(val) - ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
			
			if(p < largestPValue && 
					((val > 0 && isScoreForLargeValues) || (val < 0 && isScoreForSmallValues)) ){
				score = Math.max(a2*Math.pow(p, 2) - a1*p + 1, 0);
				score = Math.round(score*100);
			}
		}
		
		
		 
		 return new FeatureScore(outputFieldName, score);
	}
}
