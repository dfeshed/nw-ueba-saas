package fortscale.streaming.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.prevalance.FieldModel;
import fortscale.streaming.model.prevalance.calibration.FeatureCalibration;
import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesCalibratedModel implements FieldModel{
	private static Logger logger = Logger.getLogger(DiscreetValuesCalibratedModel.class);

	private FeatureCalibration featureCalibration = new FeatureCalibration();
	
	@Override
	public void add(Object value, long timestamp) {
		try {
			String featureValue = getFeatureValue(value);
			if(featureValue != null){
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
		if(featureValue != null){
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
}
