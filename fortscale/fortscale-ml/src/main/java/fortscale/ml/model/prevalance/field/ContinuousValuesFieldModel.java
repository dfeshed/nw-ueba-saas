package fortscale.ml.model.prevalance.field;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.utils.ConversionUtils;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ContinuousValuesFieldModel implements FieldModel{
	
	public static final String A1_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.a1";
	public static final String A2_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.a2";
	public static final String LARGEST_PVALUE_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.largest.p.value";
	public static final String SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.large.value";
	public static final String SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.small.value";
	public static final String MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.max.numof.histogram.elements";
	public static final String ROUND_NUMBER_CONFIG_FORMAT = "fortscale.fields.%s.continuous.model.round.number";
	
	
	private ContinuousValuesModel continuousValuesModel = new ContinuousValuesModel(1.0);
	
	@Override
	public void init(String fieldName, Config config) {
		double roundNumber = config.getDouble(String.format(ROUND_NUMBER_CONFIG_FORMAT, fieldName), 1.0);
		
		continuousValuesModel = new ContinuousValuesModel(roundNumber);
		
		double a1 = config.getDouble(String.format(A1_CONFIG_FORMAT, fieldName), continuousValuesModel.getA1());
		continuousValuesModel.setA1(a1);
		double a2 = config.getDouble(String.format(A2_CONFIG_FORMAT, fieldName), continuousValuesModel.getA2());
		continuousValuesModel.setA2(a2);
		double largestPValue = config.getDouble(String.format(LARGEST_PVALUE_CONFIG_FORMAT, fieldName), continuousValuesModel.getLargestPValue());
		continuousValuesModel.setLargestPValue(largestPValue);
		boolean scoreForLargeValues = config.getBoolean(String.format(SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, fieldName), continuousValuesModel.isScoreForLargeValues());
		continuousValuesModel.setScoreForLargeValues(scoreForLargeValues);
		boolean scoreForSmallValues = config.getBoolean(String.format(SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, fieldName), continuousValuesModel.isScoreForSmallValues());
		continuousValuesModel.setScoreForSmallValues(scoreForSmallValues);
		int maxNumOfHistogramElements = config.getInt(String.format(MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT, fieldName), continuousValuesModel.getMaxNumOfHistogramElements());
		continuousValuesModel.setMaxNumOfHistogramElements(maxNumOfHistogramElements);
	}

	@Override
	public boolean shouldSkipEvent(Object value) {
		return false;
	}

	@Override
	public void add(Object value, long timestamp) {
		Double val = ConversionUtils.convertToDouble(value);
		continuousValuesModel.add(val);
	}
	
	
	
	
	@Override
	public double calculateScore(Object value) {
		Double val = ConversionUtils.convertToDouble(value);
		return continuousValuesModel.calculateScore(val);
	}
	
	@Override
	public boolean shouldAffectEventScore() {
		return true;
	}

	public ContinuousValuesModel getContinuousValuesModel() {
		return continuousValuesModel;
	}
}
