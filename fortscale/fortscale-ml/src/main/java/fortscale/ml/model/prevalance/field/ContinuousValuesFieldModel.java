package fortscale.ml.model.prevalance.field;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.utils.ConversionUtils;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ContinuousValuesFieldModel implements FieldModel{
	
	public static final String MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT = "%s.%s.continuous.model.max.numof.histogram.elements";
	public static final String ROUND_NUMBER_CONFIG_FORMAT = "%s.%s.continuous.model.round.number";
	
	
	private ContinuousValuesModel continuousValuesModel = new ContinuousValuesModel(1.0);
	
	@Override
	public void init(String prefix, String fieldName, Config config) {
		double roundNumber = config.getDouble(String.format(ROUND_NUMBER_CONFIG_FORMAT, prefix, fieldName), 1.0);
		
		continuousValuesModel = new ContinuousValuesModel(roundNumber);
		
		int maxNumOfHistogramElements = config.getInt(String.format(MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT, prefix, fieldName), continuousValuesModel.getMaxNumOfHistogramElements());
		continuousValuesModel.setMaxNumOfHistogramElements(maxNumOfHistogramElements);
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
	
	public ContinuousValuesModel getContinuousValuesModel() {
		return continuousValuesModel;
	}
}
