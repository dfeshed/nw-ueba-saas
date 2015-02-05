package fortscale.streaming.scorer;

import static fortscale.utils.ConversionUtils.convertToString;
import net.minidev.json.JSONObject;

public class EventMessage {
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
}
