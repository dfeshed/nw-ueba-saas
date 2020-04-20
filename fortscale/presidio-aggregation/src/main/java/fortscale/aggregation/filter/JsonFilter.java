package fortscale.aggregation.filter;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * Created by orend on 12/07/2015.
 */
@JsonTypeName(JsonFilter.JSON_FILTER_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonFilter {
	public static final String JSON_FILTER_TYPE = "json_filter";
	private static final String EVENT_LIST_WRAPPER = "dummy_list";
	private String jsonPath;
	@JsonIgnore
	private JsonPath path;
	@JsonIgnore
	private boolean isAddDummyList;

	public JsonFilter(@JsonProperty("jsonPath") String jsonPath) {
		setJsonPath(jsonPath);
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
		if(!jsonPath.startsWith("$")){
			path = JsonPath.compile(String.format("$.%s%s", EVENT_LIST_WRAPPER,jsonPath));
			isAddDummyList = true;
		} else{
			path = JsonPath.compile(jsonPath);
			isAddDummyList = false;
		}
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public boolean passedFilter(JSONObject jsonObject) {
		JSONObject externalDocument = jsonObject;
		if(isAddDummyList){
			ArrayList<Object> singleObjectList = new ArrayList<Object>();
			singleObjectList.add(jsonObject);
			externalDocument = new JSONObject();
			externalDocument.put(EVENT_LIST_WRAPPER, singleObjectList);
		}
		
		DocumentContext documentContext = JsonPath.parse(externalDocument);
		Object jsonPathResult = documentContext.read(path);
		if (jsonPathResult == null) {
			throw new IllegalArgumentException(String.format("Invalid JSON path provided: %s", path));
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
