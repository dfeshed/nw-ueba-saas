package fortscale.ml.model.prevalance.field;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Counts values based on time with eviction policy that 
 * discards old stale counts
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class EvictingCountingMap {

	private Map<Object, Long> counts = new HashMap<Object, Long>();
		
	public void add(Object value) {
		if (value==null || "".equals(value))
			return;
		
		// increment value counter
		if (counts.containsKey(value)) {
			Long count = counts.get(value);
			counts.put(value, ++count);
		} else {
			counts.put(value, 1L);
		}
	}
	
	public long count(Object value) {
		if (value==null || "".equals(value))
			return 0;
		
		Long count = counts.get(value);
		return (count==null)? 0L : count;
	}
	
}
