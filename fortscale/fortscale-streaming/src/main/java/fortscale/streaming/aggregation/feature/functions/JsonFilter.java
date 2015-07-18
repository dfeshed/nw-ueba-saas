package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.JsonPath;

import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Created by orend on 12/07/2015.
 */
@JsonTypeName(JsonFilter.JSON_FILTER_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonFilter {
	public static final String JSON_FILTER_TYPE = "json_filter";
	private static final String EVENT_LIST_WRAPPER = "dummy_list";
	private String jsonPath;

	public JsonFilter(@JsonProperty("jsonPath") String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public boolean passedFilter(JSONObject jsonObject) {
		if (jsonPath == null || jsonPath.length() == 0) {
			return true;
		}
		
		if(!jsonPath.startsWith("$")){
			ArrayList<Object> singleObjectList = new ArrayList<Object>();
			singleObjectList.add(jsonObject);
			JSONObject externalDocument = new JSONObject();
			externalDocument.put(EVENT_LIST_WRAPPER, singleObjectList);
			return passedFilter(externalDocument, String.format("$.%s%s", EVENT_LIST_WRAPPER,jsonPath));
		} else{
			return passedFilter(jsonObject,jsonPath);
		}
	}
	
	private boolean passedFilter(JSONObject jsonObject, String jsonPath){
		Object jsonPathResult = JsonPath.read(jsonObject, jsonPath);
		if (jsonPathResult == null) {
			throw new IllegalArgumentException(String.format("Invalid JSON path provided: %s", jsonPath));
		}
		else if (jsonPathResult instanceof List && ((List<?>)jsonPathResult).size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		JsonFilter other = (JsonFilter) obj;
		return new EqualsBuilder().append(this.jsonPath, other.jsonPath).isEquals();
	}
}
