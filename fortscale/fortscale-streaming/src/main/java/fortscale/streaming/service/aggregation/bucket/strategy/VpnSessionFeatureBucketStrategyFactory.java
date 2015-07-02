package fortscale.streaming.service.aggregation.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class VpnSessionFeatureBucketStrategyFactory implements FeatureBucketStrategyFactory {
	public static final String STRATEGY_TYPE = "vpn_session";

	private static final String MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME = "maxSessionDuration";

	private List<VpnSessionFeatureBucketStrategy> featureBucketStrategies = new ArrayList<>();

	@Override
	public FeatureBucketStrategy createFeatureBucketStrategy(StrategyJson strategyJson) throws JsonMappingException {
		// Get Inactivity strategy parameters
		JSONObject params = strategyJson.getParams();

		// Get inactivity duration in minutes
		Long maxSessionDuration = ConversionUtils.convertToLong(params.get(MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME));
		String message = String.format("Params must contain field '%s' with a valid long value", MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME);
		Assert.notNull(maxSessionDuration, message);

		VpnSessionFeatureBucketStrategy featureBucketStrategy = new VpnSessionFeatureBucketStrategy(strategyJson.getName(), maxSessionDuration);
		featureBucketStrategies.add(featureBucketStrategy);
		return featureBucketStrategy;
	}
}
