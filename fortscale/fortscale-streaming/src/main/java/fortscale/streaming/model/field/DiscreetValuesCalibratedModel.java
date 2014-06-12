package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.FieldModel;
import fortscale.streaming.model.calibration.FeatureCalibration;
import fortscale.streaming.model.calibration.TimeFeatureCalibrationBucketScorer;
import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesCalibratedModel implements FieldModel{
	private static Logger logger = Logger.getLogger(DiscreetValuesCalibratedModel.class);

	private FeatureCalibration featureCalibration = new FeatureCalibration(TimeFeatureCalibrationBucketScorer.class);
	
	@Override
	public void add(Object value, long timestamp) {
		try {
			featureCalibration.incrementFeatureValue(value);
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DiscreetValuesCalibratedModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}
}
