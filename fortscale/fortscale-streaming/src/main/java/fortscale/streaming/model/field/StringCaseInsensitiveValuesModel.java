package fortscale.streaming.model.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class StringCaseInsensitiveValuesModel extends DiscreetValuesModel {

	@Override
	public void add(Object value, long timestamp) {
		if (value==null || !(value instanceof String) || "".equals(value))
			return;
		
		String str = (String)value;
		super.add(str.toLowerCase(), timestamp);
	}
}
