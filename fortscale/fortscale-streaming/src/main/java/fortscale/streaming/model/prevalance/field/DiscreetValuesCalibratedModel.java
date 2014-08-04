package fortscale.streaming.model.prevalance.field;

import java.util.LinkedList;
import java.util.List;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.prevalance.FieldModel;
import fortscale.streaming.model.prevalance.calibration.FeatureCalibration;
import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesCalibratedModel implements FieldModel{
	private static Logger logger = Logger.getLogger(DiscreetValuesCalibratedModel.class);

	private static final String BOOST_VALUES_CONFIG_FORMAT = "fortscale.fields.%s.boost.score.values";
	private static final String IGNORE_VALUES_CONFIG_FORMAT = "fortscale.fields.%s.ignore.score.values";
	
	private FeatureCalibration featureCalibration = new FeatureCalibration();
	private List<String> boostValues;
	private List<String> ignoreValues;
	
	@Override
	public void init(String fieldName, Config config) {
		// get the boost and ignore score values from configuration
		boostValues = config.getList(String.format(BOOST_VALUES_CONFIG_FORMAT, fieldName), new LinkedList<String>());
		ignoreValues = config.getList(String.format(IGNORE_VALUES_CONFIG_FORMAT, fieldName), new LinkedList<String>());
	}
	
	@Override
	public void add(Object value, long timestamp) {
		try {
			String featureValue = getFeatureValue(value);
			// boost scores values should be skipped from the model so that they will receive high 
			// score in calculateScore method
			if(featureValue != null && !isBoostValue(featureValue) && !isIgnoreValue(featureValue)) {
				featureCalibration.incrementFeatureValue(featureValue);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DiscreetValuesCalibratedModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}

	@Override
	public double calculateScore(Object value) {
		String featureValue = getFeatureValue(value);
		if(featureValue != null) {
			if (isIgnoreValue(featureValue))
				return 0;
			return featureCalibration.score(value.toString());
		} else{
			return 0;
		}
	}
	
	private String getFeatureValue(Object value){
		if(value == null){
			return null;
		}
		return value.toString();
	}
	
	private boolean isBoostValue(String value) {
		return boostValues.contains(value);
	}
	
	private boolean isIgnoreValue(String value) {
		return ignoreValues.contains(value);
	}

	@Override
	public boolean shouldAffectEventScore() {
		return true;
	}
}
