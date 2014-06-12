package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.FieldModel;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DiscreetValuesModel implements FieldModel {

	private EvictingCountingMap counters = new EvictingCountingMap();
	
	@Override
	public void add(Object value, long timestamp) {
		counters.add(value);
	}
}
