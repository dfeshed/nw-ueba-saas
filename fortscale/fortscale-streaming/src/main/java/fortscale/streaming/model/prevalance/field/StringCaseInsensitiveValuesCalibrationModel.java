package fortscale.streaming.model.prevalance.field;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class StringCaseInsensitiveValuesCalibrationModel extends DiscreetValuesCalibratedModel {
	private static Logger logger = Logger.getLogger(StringCaseInsensitiveValuesCalibrationModel.class);

	@Override
	public void add(Object value, long timestamp) {
		if(value == null){
			return;
		}
		if (!isValueValid(value)){
			logger.warn("given value ({}) is not valid", value);
			return;
		}
		
		if(StringUtils.isNotEmpty((String) value)){
			String str = (String)value;
			super.add(str.toLowerCase(), timestamp);
		}
	}
	
	@Override
	public double calculateScore(Object value) {
		if(value == null){
			return 0;
		}
		double ret = 0;
		if (isValueValid(value)){
			if(value != null && StringUtils.isNotEmpty((String) value)){
				String str = (String)value;
				ret = super.calculateScore(str.toLowerCase());
			}
		} else{
			logger.warn("given value ({}) is not valid", value);
		}
		
		return ret;
	}
	
	private boolean isValueValid(Object value){
		return value instanceof String;
	}
}
