package fortscale.common.event;

import net.minidev.json.JSONObject;
import static fortscale.utils.ConversionUtils.convertToString;

public class EventMessage implements Event{
	private JSONObject jsonObject;
	
	public EventMessage(JSONObject jsonObject){
		this.jsonObject = jsonObject;
	}
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	
	public String getEventStringValue(String key){
		return convertToString(jsonObject.get(key));
	}
	
	public String toJSONString(){
		return jsonObject.toJSONString();
	}

	@Override
	public Object get(String key) {
		return jsonObject.get(key);
	}

	@Override
	public JSONObject getJSONObject() {
		return jsonObject;
	}

	@Override
	public String getDataSource() {
		throw new UnsupportedOperationException();
	}
}
