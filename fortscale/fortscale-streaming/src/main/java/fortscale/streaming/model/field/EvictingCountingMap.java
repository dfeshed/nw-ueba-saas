package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.common.collect.HashMultiset;

/**
 * Counts values based on time with eviction policy that 
 * discards old stale counts
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class EvictingCountingMap {

	private HashMultiset<Object> counts = HashMultiset.create();
	
	public EvictingCountingMap() {
	}
	
	
	public void add(Object value) {
		if (value==null || "".equals(value))
			return;
		
		counts.add(value);
	}
	
	public int count(Object value) {
		if (value==null || "".equals(value))
			return 0;
		
		return counts.count(value);
	}
	
}
