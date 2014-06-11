package fortscale.streaming.model.field;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.FieldModel;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesModel implements FieldModel {

	private EvictingCountingMap counters = new EvictingCountingMap();
	private long timeMark;
	
	@Override
	public void add(Object value, long timestamp) {
		long millis = convertToMilliSeconds(timestamp);
		if (millis >= timeMark) {
			counters.add(value);
			timeMark = millis;
		}
	}
	
	@Override
	public long getTimeMark() {
		return timeMark;
	}
	
	@Override
	public boolean isTimeMarkAfter(long timestamp) {
		long millis = convertToMilliSeconds(timestamp);
		return (timeMark > millis);
	}
}
