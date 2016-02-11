package fortscale.common.event;

import net.minidev.json.JSONObject;

public interface Event {
	Object get(String key);
	JSONObject getJSONObject();
	String getDataSource();
}
