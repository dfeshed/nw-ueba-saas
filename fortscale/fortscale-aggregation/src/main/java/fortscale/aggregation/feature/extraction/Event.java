package fortscale.aggregation.feature.extraction;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import net.minidev.json.JSONObject;

public class Event {

	private JSONObject jsonObject;
	private DataEntitiesConfig dataEntitiesConfig;
	private String eventType;
	
	public Event(JSONObject jsonObject, DataEntitiesConfig dataEntitiesConfig, String eventType){
		this.jsonObject = jsonObject;
		this.dataEntitiesConfig = dataEntitiesConfig;
		this.eventType = eventType;
	}
	
	public Object get(String key) throws InvalidQueryException{
		if(eventType != null){
			return jsonObject.get(dataEntitiesConfig.getFieldColumn(eventType, key));
		} else{
			return jsonObject.get(key);
		}
	}
}
