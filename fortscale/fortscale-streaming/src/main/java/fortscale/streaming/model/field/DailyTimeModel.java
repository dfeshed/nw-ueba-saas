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
			if(value instanceof Long){
				super.update(TimestampUtils.convertToSeconds((Long) value));
			} else{
				logger.error("got value {} of instance {} instead of Long", value, value.getClass());
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}

	@Override
	public double calculateScore(Object value) {
		double ret = 0;
		try {
			if(value instanceof Long){
				long epochSeconds=TimestampUtils.convertToSeconds((Long) value);
				super.score(epochSeconds);
			} else{
				logger.error("got value {} of instance {} instead of Long", value, value.getClass());
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
		
		return ret;
	}
}
