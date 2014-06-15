package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class StringCaseInsensitiveValuesCalibrationModel extends DiscreetValuesCalibratedModel {
	private static Logger logger = Logger.getLogger(StringCaseInsensitiveValuesCalibrationModel.class);

	@Override
	public void add(Object value, long timestamp) {
		if (isValueValid(value)){		
			String str = (String)value;
			super.add(str.toLowerCase(), timestamp);
		} else{
			logger.warn("given value ({}) is not valid", value);
		}
	}
	
	@Override
	public double calculateScore(Object value) {
		double ret = 0;
		if (isValueValid(value)){
			String str = (String)value;
			 ret = super.calculateScore(str.toLowerCase());
		} else{
			logger.warn("given value ({}) is not valid", value);
		}
		
		return ret;
	}
	
	private boolean isValueValid(Object value){
		return !(value==null || !(value instanceof String) || "".equals(value));
	}
}
