package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Map;

public class EntityEvent implements Event {
	//TODO: remove one fo the duplicates - fortscale.domain.core.EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME
	// This field is a duplicate of the fortscale.domain.core.EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME.
	// It is defined here as well in order not to add dependency on the fortscale.domain.core package.
	private static final String ENTITY_EVENT_CONTEXT_FIELD_NAME = "context";
	private JSONObject message;
	private String dataSource;

	public EntityEvent(JSONObject message, String dataSource) {
		Assert.notNull(message);
		Assert.notNull(dataSource);
		this.message = message;
		this.dataSource = dataSource;
	}

	@Override
	public Object get(String key) {
		if(isKeyContextFieldName(key)){
			return extractValueFromJson(key);
		} else{
			return message.get(key);
		}
	}

	private boolean isKeyContextFieldName(String key){
		String featurePathElems[] = key.split("\\.");
		if(featurePathElems.length != 2){
			return false;
		}
		if(!featurePathElems[0].equals(EntityEvent.ENTITY_EVENT_CONTEXT_FIELD_NAME)){
			return true;
		}

		return true;
	}
	private Object extractValueFromJson(String key){
		String featurePathElems[] = key.split("\\.");
		Map<?, ?> jsonObjectTmp = message;
		try{
			for(int i = 0; i<featurePathElems.length-1; i++){
				jsonObjectTmp = (Map<?, ?>) jsonObjectTmp.get(featurePathElems[i]);
			}
		} catch(Exception e){
			return null;
		}

		if(jsonObjectTmp == null){
			return null;
		}

		return jsonObjectTmp.get(featurePathElems[featurePathElems.length-1]);
	}


	@Override
	public JSONObject getJSONObject() {
		return message;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}
}
