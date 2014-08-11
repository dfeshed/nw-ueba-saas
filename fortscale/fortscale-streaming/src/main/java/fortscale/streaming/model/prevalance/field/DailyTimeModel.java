package fortscale.streaming.model.prevalance.field;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.prevalance.FieldModel;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DailyTimeModel extends TimeModel implements FieldModel{
	private static Logger logger = Logger.getLogger(DailyTimeModel.class);
	
	public static final int TIME_RESOLUTION = 60 * 60 * 24 ;
	private static final int BUCKET_SIZE = 60 * 10;
	
	public DailyTimeModel(){
		super(TIME_RESOLUTION, BUCKET_SIZE);
	}
	
	@Override
	public void init(String fieldName, Config config) {}
	
	@Override
	public boolean shouldSkipEvent(Object value) {
		return false;
	}
	
	@Override
	public void add(Object value, long timestamp){
		try {
			Long epoch = convertToSeconds(value);
			if(epoch != null){
				super.update(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}
	
	private Long convertToSeconds(Object value){
		if(value == null){
			return null;
		}
		
		Long ret = null;
		if(value instanceof Long){
			ret = (Long)value;
		} else if(value instanceof Long || value instanceof Integer){
			ret = ((Integer) value).longValue();
		} else if(value instanceof String){
			try{
				if(StringUtils.isNotEmpty((String) value)){
					ret = Long.parseLong((String) value);
				}
			} catch(NumberFormatException nfe){
				logger.warn("got the String value ({}) which is not a Long as expected.");
			}
		} else{
			logger.warn("got value {} of instance {} instead of Long or String", value, value.getClass());
		}
		
		if(ret != null){
			ret = TimestampUtils.convertToSeconds(ret);
		}
		return ret;
	}

	@Override
	public double calculateScore(Object value) {
		double ret = 0;
		try {
			Long epoch = convertToSeconds(value);
			if(epoch != null){
				ret = super.score(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
		
		return ret;
	}

	@Override
	public boolean shouldAffectEventScore() {
		return true;
	}
}
