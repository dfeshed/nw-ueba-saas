package fortscale.streaming.scorer;

import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONObject;

public class EventMessage {
	private JSONObject jsonObject;
	private Map<String, Double> scoreMap = new HashMap<>();
	
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
	
	
	
	public Iterator<Entry<String, Double>> getScoreIterator() {
		return scoreMap.entrySet().iterator();
	}

	public Double getScore(String scoreName){
		return scoreMap.get(scoreName);
	}
	
	public void setScore(String scoreName, Double score){
		scoreMap.put(scoreName, score);
	}
}
