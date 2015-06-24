package fortscale.streaming.service.aggregation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggregatedFeatureConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> featureNames;
    private JSONObject aggrFeatureFuncJson;

	public AggregatedFeatureConf(@JsonProperty("name")String name,
								 @JsonProperty("featureNames")List<String> featureNames,
								 @JsonProperty("aggrFeatureFuncJson")JSONObject aggrFeatureFuncJson) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(featureNames);
		for (String featureName : featureNames) {
			Assert.isTrue(StringUtils.isNotBlank(featureName));
		}
        Assert.notNull(aggrFeatureFuncJson);

		this.name = name;
		this.featureNames = featureNames;
        this.aggrFeatureFuncJson = aggrFeatureFuncJson;
	}

	public String getName() {
		return name;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}

    public String getAggrFeatureFuncJson() { return aggrFeatureFuncJson.toJSONString(); }
}
