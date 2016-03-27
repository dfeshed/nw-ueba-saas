package fortscale.common.event;

import net.minidev.json.JSONObject;

public class RawEvent extends AbstractEvent{

	private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;
	
	public RawEvent(JSONObject jsonObject, DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList, String dataSource){
		super(jsonObject, dataSource);
		this.dataEntitiesConfigWithBlackList = dataEntitiesConfigWithBlackList;
	}
	
	@Override
	public Object get(String key){
		String fieldColumn = key;
		if(dataSource != null){
			String tmp = dataEntitiesConfigWithBlackList.getFieldColumn(dataSource, key);
			if(tmp != null){
				fieldColumn = tmp;
			}
		}
		
		return jsonObject.get(fieldColumn);
	}

}
