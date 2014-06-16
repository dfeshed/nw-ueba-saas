package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.FieldModel;
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
	public void add(Object value, long timestamp){
		try {
			Long epoch = convertToLong(value);
			if(epoch != null){
				super.update(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}
	
	private Long convertToLong(Object value){
		Long ret = null;
		if(value instanceof Long){
			ret = TimestampUtils.convertToSeconds((Long) value);
		} else if(value instanceof String){
			try{
				ret = Long.parseLong((String) value);
			} catch(NumberFormatException nfe){
				logger.warn("got the String value ({}) which is not a Long as expected.");
			}
		} else{
			logger.warn("got value {} of instance {} instead of Long or String", value, value.getClass());
		}
		
		return ret;
	}

	@Override
	public double calculateScore(Object value) {
		double ret = 0;
		try {
			Long epoch = convertToLong(value);
			if(epoch != null){
				super.score(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
		
		return ret;
	}
}
