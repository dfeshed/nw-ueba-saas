package fortscale.streaming.service.aggregation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggregatedFeatureConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Map<String, List<String>> featureNamesMap;
	private Set<String> allFeatureNames;
	private JSONObject aggrFeatureFuncJson;

	public AggregatedFeatureConf(
			@JsonProperty("name") String name,
			@JsonProperty("featureNamesMap") Map<String, List<String>> featureNamesMap,
			@JsonProperty("aggrFeatureFuncJson") JSONObject aggrFeatureFuncJson) {

		// Validate name
		Assert.isTrue(StringUtils.isNotBlank(name));

		// Validate featureNamesMap
		Assert.notEmpty(featureNamesMap);
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
}
