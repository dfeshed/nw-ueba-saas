package fortscale.streaming.service.aggregation.bucket.strategy;

import fortscale.streaming.service.aggregation.FeatureBucketConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import java.util.List;

public class InactivityFeatureBucketStrategy implements FeatureBucketStrategy {
	private String strategyName;
	private List<String> dataSources;
	private long inactivityDurationInMinutes;

	public InactivityFeatureBucketStrategy(String strategyName, List<String> dataSources, long inactivityDurationInMinutes) {
		// Validate input
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.notEmpty(dataSources);
		Assert.isTrue(inactivityDurationInMinutes > 0);

		this.strategyName = strategyName;
		this.dataSources = dataSources;
		this.inactivityDurationInMinutes = inactivityDurationInMinutes;
	}

	@Override
	public FeatureBucketStrategyData update(JSONObject event) {
		return null;
	}

	@Override
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(FeatureBucketConf featureBucketConf, JSONObject event, long epochtimeInSec) {
		return null;
	}
}
