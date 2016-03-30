package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Event {
	static final String ERR_MSG_CONTEXT_VALUE_IS_NOT_OF_TYPE_STRING = "Returned context field value is not of type String: %s, jsonObject: %s";

	Object get(String key);
	JSONObject getJSONObject();

	default String getContextField(String key) {
		Object context = get(key);
		if(context!=null) {
			Assert.isInstanceOf(String.class, context, String.format(ERR_MSG_CONTEXT_VALUE_IS_NOT_OF_TYPE_STRING, context.toString(), getJSONObject().toJSONString()));
		}
		return (String)context;
	}


	default String getDataSource() {
		throw new UnsupportedOperationException();
	}

	default Map<String,String> getContextFields(List<String> contextFieldNames) {
		Map<String, String> contextFields = new HashMap<>();
		for(String contextFieldName: contextFieldNames) {
			contextFields.put(contextFieldName, getContextField(contextFieldName));
		}
		return contextFields;
	}
}
