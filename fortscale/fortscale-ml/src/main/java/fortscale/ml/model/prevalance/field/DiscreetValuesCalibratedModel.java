package fortscale.ml.model.prevalance.field;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.calibration.FeatureCalibration;
import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesCalibratedModel implements FieldModel{
	private static Logger logger = Logger.getLogger(DiscreetValuesCalibratedModel.class);

	private static final String IGNORE_VALUES_CONFIG_FORMAT = "%s.%s.ignore.score.regex";
	
	private FeatureCalibration featureCalibration = new FeatureCalibration();
	private Pattern ignoreValues;
	
	
	public int getNumOfFeatureValues(){
		return featureCalibration.getNumOfFeatureValues();
	}
	
	@Override
	public void init(String prefix, String fieldName, Config config) {
		// get the ignore score values from configuration
		
		String ignorePattern = config.get(String.format(IGNORE_VALUES_CONFIG_FORMAT, prefix, fieldName));
		if (ignorePattern!=null)
			ignoreValues = Pattern.compile(ignorePattern);
	}
	
	@Override
	public void add(Object value, long timestamp) {
		try {
			String featureValue = getFeatureValue(value);
			// boost scores values should be skipped from the model so that they will receive high 
			// score in calculateScore method
			if(featureValue != null && !isIgnoreValue(featureValue)) {
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
	
	private boolean isIgnoreValue(String value) {
		if(StringUtils.isBlank(value)){
			return true;
		}
		return (ignoreValues==null)? false : ignoreValues.matcher(value).matches();
	}	
}
