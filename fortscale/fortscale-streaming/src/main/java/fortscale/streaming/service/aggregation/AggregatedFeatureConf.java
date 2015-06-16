package fortscale.streaming.service.aggregation;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import java.util.List;

public class AggregatedFeatureConf {
	private String name;
	private List<String> featureNames;
	private String aggregatedFeatureHandlerName;

	public AggregatedFeatureConf(String name, List<String> featureNames, String aggregatedFeatureHandlerName) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(featureNames);
		for (String featureName : featureNames) {
			Assert.isTrue(StringUtils.isNotBlank(featureName));
		}
		Assert.isTrue(StringUtils.isNotBlank(aggregatedFeatureHandlerName));

		this.name = name;
		this.featureNames = featureNames;
		this.aggregatedFeatureHandlerName = aggregatedFeatureHandlerName;
	}

	public String getName() {
		return name;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}

	public String getAggregatedFeatureHandlerName() {
		return aggregatedFeatureHandlerName;
	}
}
