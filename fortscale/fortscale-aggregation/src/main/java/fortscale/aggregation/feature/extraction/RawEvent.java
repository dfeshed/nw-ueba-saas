package fortscale.aggregation.feature.extraction;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
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
	
	@Override
	public Object get(String key){
		if(eventType != null){
			try {
				return jsonObject.get(dataEntitiesConfig.getFieldColumn(eventType, key));
			} catch (InvalidQueryException e) {
				throw new RuntimeException("got an exception while trying to get value from the event", e);
			}
		} else{
			return jsonObject.get(key);
		}
	}

	@Override
	public JSONObject getJSONObject() {
		return jsonObject;
	}
}
