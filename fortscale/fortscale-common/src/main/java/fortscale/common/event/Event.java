package fortscale.common.event;

import net.minidev.json.JSONObject;

public interface Event {
	public Object get(String key);
	public JSONObject getJSONObject();
}
