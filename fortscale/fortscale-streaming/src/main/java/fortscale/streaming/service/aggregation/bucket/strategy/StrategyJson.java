package fortscale.streaming.service.aggregation.bucket.strategy;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonMappingException;

import net.minidev.json.JSONObject;

public class StrategyJson {
	private JSONObject jsonObject;
	
	private static final String	JSON_CONF_STRATEGY_TYPE_FIELD_NAME = 	"type";
	private static final String	JSON_CONF_STRATEGY_NAME_FIELD_NAME = 	"name";
	private static final String	JSON_CONF_STRATEGY_PARAMS_FIELD_NAME = 	"params";
	
	public StrategyJson(@NotNull JSONObject jsonObject) throws JsonMappingException{
		this.jsonObject = jsonObject;
		if(getName()==null || getType()==null || getParams()==null){
			throw new JsonMappingException(String.format("json object %s doesn't contain all fields %s", jsonObject.toJSONString()));
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
