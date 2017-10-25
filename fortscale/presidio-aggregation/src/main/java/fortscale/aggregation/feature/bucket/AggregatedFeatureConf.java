package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.aggregation.filter.JsonFilter;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY,
		getterVisibility = Visibility.NONE,
		isGetterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE
)
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
		Assert.hasText(name, "name cannot be blank.");

		// Validate featureNamesMap
		for (Map.Entry<String, List<String>> entry : featureNamesMap.entrySet()) {
			Assert.hasText(entry.getKey(), "featureNamesMap keys cannot be blank.");
			Assert.notEmpty(entry.getValue(), "featureNamesMap values cannot be empty.");
			for (String featureName : entry.getValue()) {
				Assert.hasText(featureName, "featureNames cannot be blank.");
			}
		}

		// Validate aggrFeatureFuncJson
		Assert.notNull(aggrFeatureFuncJson, "aggrFeatureFuncJson cannot be null.");

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

	public boolean passedFilter(AdeRecordReader jsonObject) {
		if (filter == null)
		{
			return true;
		}
		else {
			JSONObject adeRecordAsJsonObject = jsonObject.getAdeRecordAsJsonObject();
			return passedFilter(adeRecordAsJsonObject);
		}
	}

	private boolean passedFilter(JSONObject adeRecordAsJsonObject) {
		return filter.passedFilter(adeRecordAsJsonObject);
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
		return new EqualsBuilder()
				.append(this.name, other.name)
				.append(this.aggrFeatureFuncJson, other.aggrFeatureFuncJson)
				.append(this.allFeatureNames, other.allFeatureNames)
				.append(this.filter, other.filter)
				.isEquals();
	}
}
