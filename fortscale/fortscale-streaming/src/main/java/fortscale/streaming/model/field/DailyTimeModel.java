package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.FieldModel;
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
			super.update((Long) value);
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}
}
