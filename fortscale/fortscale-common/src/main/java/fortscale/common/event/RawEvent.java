package fortscale.common.event;

import net.minidev.json.JSONObject;

public class RawEvent implements Event{

	private JSONObject jsonObject;
	private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;
	private String eventType;
	
	public RawEvent(JSONObject jsonObject, DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList, String eventType){
		this.jsonObject = jsonObject;
		this.dataEntitiesConfigWithBlackList = dataEntitiesConfigWithBlackList;
		this.eventType = eventType;
	}
	
	@Override
	public Object get(String key){
		String fieldColumn = key;
		if(eventType != null){
			String tmp = dataEntitiesConfigWithBlackList.getFieldColumn(eventType, key);
			if(tmp != null){
				fieldColumn = tmp;
			}
		}
		
		return jsonObject.get(fieldColumn);
	}

	@Override
	public JSONObject getJSONObject() {
		return jsonObject;
	}

	@Override
	public String getDataSource() {
		return eventType;
	}
}
