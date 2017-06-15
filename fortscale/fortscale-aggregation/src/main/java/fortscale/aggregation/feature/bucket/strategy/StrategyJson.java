package fortscale.aggregation.feature.bucket.strategy;

import net.minidev.json.JSONObject;

import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonMappingException;

public class StrategyJson {
	private JSONObject jsonObject;
	
	private static final String	JSON_CONF_STRATEGY_TYPE_FIELD_NAME = 	"type";
	private static final String	JSON_CONF_STRATEGY_NAME_FIELD_NAME = 	"name";
	private static final String	JSON_CONF_STRATEGY_PARAMS_FIELD_NAME = 	"params";
	
	public StrategyJson(JSONObject jsonObject) throws JsonMappingException{
		Assert.notNull(jsonObject);
		this.jsonObject = jsonObject;
		if(getName()==null || getType()==null || getParams()==null){
			throw new JsonMappingException(String.format("json object doesn't contain all fields: %s", jsonObject.toJSONString()));
		}
	}
	
	public String getName(){
		return (String) jsonObject.get(JSON_CONF_STRATEGY_NAME_FIELD_NAME);
	}
	
	public String getType(){
		return (String) jsonObject.get(JSON_CONF_STRATEGY_TYPE_FIELD_NAME);
	}
	
	public JSONObject getParams(){
		return (JSONObject) jsonObject.get(JSON_CONF_STRATEGY_PARAMS_FIELD_NAME);
	}
}
