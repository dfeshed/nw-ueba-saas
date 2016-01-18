package fortscale.aggregation.feature.bucket;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import fortscale.common.event.Event;
import fortscale.aggregation.filter.JsonFilter;
import net.minidev.json.JSONObject;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggregatedFeatureConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Map<String, List<String>> featureNamesMap;
	private Set<String> allFeatureNames;
    private JSONObject aggrFeatureFuncJson;
    private JsonFilter filter;

	public AggregatedFeatureConf(
			@JsonProperty("name") String name,
			@JsonProperty("featureNamesMap") Map<String, List<String>> featureNamesMap,
			@JsonProperty("aggrFeatureFuncJson") JSONObject aggrFeatureFuncJson) {

		// Validate name
		Assert.isTrue(StringUtils.isNotBlank(name));

		// Validate featureNamesMap
		for (Map.Entry<String, List<String>> entry : featureNamesMap.entrySet()) {
			Assert.isTrue(StringUtils.isNotBlank(entry.getKey()));
			Assert.notEmpty(entry.getValue());
			for (String featureName : entry.getValue()) {
				Assert.isTrue(StringUtils.isNotBlank(featureName));
			}
		}

		// Validate aggrFeatureFuncJson
		Assert.notNull(aggrFeatureFuncJson);

		this.name = name;
		this.featureNamesMap = featureNamesMap;
		this.allFeatureNames = new HashSet<>();
		for (List<String> featureNames : featureNamesMap.values()) {
			this.allFeatureNames.addAll(featureNames);
		}
		this.aggrFeatureFuncJson = aggrFeatureFuncJson;
	}

	public String getName() {
		return name;
	}

	public Map<String, List<String>> getFeatureNamesMap() {
		return featureNamesMap;
	}

	public Set<String> getAllFeatureNames() {
		return allFeatureNames;
	}

	public JSONObject getAggrFeatureFuncJson() {
		return aggrFeatureFuncJson;
	}
    
    public void setFilter(JsonFilter filter) {
		this.filter = filter;
	}

	public boolean passedFilter(Event event){
    	return filter == null ? true : filter.passedFilter(event.getJSONObject());
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
		AggregatedFeatureConf other = (AggregatedFeatureConf) obj;
		return new EqualsBuilder().append(this.name, other.name).append(this.aggrFeatureFuncJson, other.aggrFeatureFuncJson).append(this.allFeatureNames, other.allFeatureNames).append(this.filter, other.filter).isEquals();
	}
}
