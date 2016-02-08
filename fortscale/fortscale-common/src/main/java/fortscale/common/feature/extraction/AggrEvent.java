package fortscale.common.feature.extraction;

import fortscale.common.event.Event;
import net.minidev.json.JSONObject;
import java.util.Map;

public class AggrEvent implements Event {
	private JSONObject jsonObject;
	private String aggrFeatureNameFieldName;
	private String aggrFeatureValueFieldName;
	private String bucketConfFieldName;
	private String dataSource;

	public AggrEvent(
			JSONObject jsonObject, String aggrFeatureNameFieldName, String aggrFeatureValueFieldName,
			String bucketConfFieldName, String dataSource) {

		this.jsonObject = jsonObject;
		this.aggrFeatureNameFieldName = aggrFeatureNameFieldName;
		this.aggrFeatureValueFieldName = aggrFeatureValueFieldName;
		this.bucketConfFieldName = bucketConfFieldName;
		this.dataSource = dataSource;
	}

	@Override
	public Object get(String key){
		if(isKeyFeatureName(key)){
			return jsonObject.get(aggrFeatureValueFieldName);
		} else{
			return extractValueFromJson(key);
		}
	}
	
	private boolean isKeyFeatureName(String key){
		String featurePathElems[] = key.split("\\.");
		if(featurePathElems.length != 2){
			return false;
		}
		if(!featurePathElems[0].equals(jsonObject.get(bucketConfFieldName))){
			return false;
		}
		if(!featurePathElems[1].equals(jsonObject.get(aggrFeatureNameFieldName))){
			return false;
		}
		
		return true;
	}
	
	private Object extractValueFromJson(String key){
		String featurePathElems[] = key.split("\\.");
		Map<?, ?> jsonObjectTmp = jsonObject;
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
		return jsonObject;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}
}
