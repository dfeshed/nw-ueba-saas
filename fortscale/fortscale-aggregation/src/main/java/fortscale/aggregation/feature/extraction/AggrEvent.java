package fortscale.aggregation.feature.extraction;

import java.util.Map;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import net.minidev.json.JSONObject;

@Configurable(preConstruction = true)
public class AggrEvent implements Event{
	private JSONObject jsonObject;
	@Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
	
	public AggrEvent(JSONObject jsonObject){
		this.jsonObject = jsonObject;
	}
	
	@Override
	public Object get(String key){
		if(aggrFeatureNameFieldName.equals(key)){
			return jsonObject.get(aggrFeatureValueFieldName);
		} else{
			return extractValueFromJson(key);
		}
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
}
