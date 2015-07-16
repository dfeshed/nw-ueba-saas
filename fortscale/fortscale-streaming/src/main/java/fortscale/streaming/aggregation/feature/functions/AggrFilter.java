package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by orend on 12/07/2015.
 */
@JsonTypeName(AggrFilter.AGGR_FILTER_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFilter {
	static final String AGGR_FILTER_TYPE = "aggr_filter";
	private String jsonPath;

	public AggrFilter(@JsonProperty("jsonPath") String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public boolean passedFilter(String objectName, Object objectValue) {
		if (jsonPath == null || jsonPath.length() == 0) {
			return true;
		}
		else {
			JSONObject document = new JSONObject();
			document.put("value", objectValue);
			ArrayList<Object> singleObjectList = new ArrayList<Object>();
			singleObjectList.add(document);
			JSONObject externalDocument = new JSONObject();
			externalDocument.put(objectName, singleObjectList);

			Object jsonPathResult = JsonPath.read(externalDocument, jsonPath);
			if (jsonPathResult == null) {
				throw new IllegalArgumentException(String.format("Invalid JSON path provided: %s", jsonPath));
			}
			else if (jsonPathResult instanceof List && ((List<?>)jsonPathResult).size() > 0) {
				return true;
			}
		}
		return false;
	}
}
