package fortscale.streaming.service.aggregation;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

public class AggregatedFeatureConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> featureNames;
    private String aggrFeatureFuncJson;

	public AggregatedFeatureConf(String name,
								 List<String> featureNames,
                                 String aggrFeatureFuncJson) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(featureNames);
		for (String featureName : featureNames) {
			Assert.isTrue(StringUtils.isNotBlank(featureName));
		}
        Assert.isTrue(StringUtils.isNotBlank(aggrFeatureFuncJson));

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

    public String getAggrFeatureFuncJson() { return aggrFeatureFuncJson; }
}
