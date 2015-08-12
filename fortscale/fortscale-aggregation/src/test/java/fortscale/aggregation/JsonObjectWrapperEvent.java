package fortscale.aggregation;

import fortscale.aggregation.feature.extraction.Event;
import net.minidev.json.JSONObject;

public class JsonObjectWrapperEvent implements Event {
	
	private JSONObject jsonobj;
	
	public JsonObjectWrapperEvent(JSONObject jsonobj) {
		this.jsonobj = jsonobj;
	}

	@Override
	public Object get(String key) {
		return jsonobj.get(key);
	}

	@Override
	public JSONObject getJSONObject() {
		return jsonobj;
	}

}
