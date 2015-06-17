package fortscale.streaming.service.aggregation;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import java.util.List;

public class AggregatedFeatureConf {
	private String name;
	private List<String> featureNames;
	private String aggrFeatureFuncName;
    private String aggrFeatureFuncJson;

	public AggregatedFeatureConf(String name, List<String> featureNames,
                                 String aggrFeatureFuncName,
                                 String aggrFeatureFuncJson) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(featureNames);
		for (String featureName : featureNames) {
			Assert.isTrue(StringUtils.isNotBlank(featureName));
		}
		Assert.isTrue(StringUtils.isNotBlank(aggrFeatureFuncName));
        Assert.isTrue(StringUtils.isNotBlank(aggrFeatureFuncJson));

		this.name = name;
		this.featureNames = featureNames;
		this.aggrFeatureFuncName = aggrFeatureFuncName;
        this.aggrFeatureFuncJson = aggrFeatureFuncJson;
	}

	public String getName() {
		return name;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}

	public String getAggrFeatureFuncName() {
		return aggrFeatureFuncName;
	}

    public String getAggrFeatureFuncJson() { return aggrFeatureFuncJson; }
}
