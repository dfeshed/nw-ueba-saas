package fortscale.streaming.model.field;

import fortscale.streaming.model.FieldModel;

public class DiscreetValuesModel implements FieldModel {

	private EvictingCountingMap counters = new EvictingCountingMap();
	
	@Override
	public void add(Object value, long timestamp) {
		counters.add(value);
	}
	
}
