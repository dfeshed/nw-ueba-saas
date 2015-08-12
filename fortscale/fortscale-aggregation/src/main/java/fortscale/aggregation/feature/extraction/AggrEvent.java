package fortscale.aggregation.feature.extraction;

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
			return null;
		}
	}

	@Override
	public JSONObject getJSONObject() {
		return jsonObject;
	}
}
