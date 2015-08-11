package fortscale.aggregation.feature.extraction;

import fortscale.services.dataentity.DataEntitiesConfig;
import net.minidev.json.JSONObject;

public class RawEvent implements Event{

	private JSONObject jsonObject;
	private DataEntitiesConfig dataEntitiesConfig;
	private String eventType;
	
	public RawEvent(JSONObject jsonObject, DataEntitiesConfig dataEntitiesConfig, String eventType){
		this.jsonObject = jsonObject;
		this.dataEntitiesConfig = dataEntitiesConfig;
		this.eventType = eventType;
	}
	
	public Object get(String key) throws Exception{
		if(eventType != null){
			return jsonObject.get(dataEntitiesConfig.getFieldColumn(eventType, key));
		} else{
			return jsonObject.get(key);
		}
	}
}
