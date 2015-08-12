package fortscale.aggregation.feature.extraction;

import net.minidev.json.JSONObject;

public interface Event {
	public Object get(String key);
	public JSONObject getJSONObject();
}
