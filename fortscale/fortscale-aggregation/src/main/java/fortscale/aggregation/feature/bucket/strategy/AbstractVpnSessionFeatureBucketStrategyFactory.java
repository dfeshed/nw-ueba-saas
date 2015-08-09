package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVpnSessionFeatureBucketStrategyFactory implements FeatureBucketStrategyFactory{
	public static final String STRATEGY_TYPE = "vpn_session";
	private static final String MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME = "maxSessionDuration";
	private static final String MISSING_MAX_SESSION_DURATION_FIELD_ERROR_MESSAGE = String.format("Params must contain field '%s' with a valid long value", MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME);

	protected List<VpnSessionFeatureBucketStrategy> featureBucketStrategies = new ArrayList<>();

	protected abstract VpnSessionFeatureBucketStrategy createVpnSessionFeatureBucketStrategy(String strategyName, long maxSessionDuration);

	@Override
	public FeatureBucketStrategy createFeatureBucketStrategy(StrategyJson strategyJson) throws JsonMappingException {
		// Get vpnsession strategy parameters
		JSONObject params = strategyJson.getParams();

		// Get vpnsession max duration parameter
		Long maxSessionDuration = ConversionUtils.convertToLong(params.get(MAX_SESSION_DURATION_JSON_PARAM_FIELD_NAME));
		Assert.notNull(maxSessionDuration, MISSING_MAX_SESSION_DURATION_FIELD_ERROR_MESSAGE);

		VpnSessionFeatureBucketStrategy featureBucketStrategy = createVpnSessionFeatureBucketStrategy(strategyJson.getName(), maxSessionDuration);
		featureBucketStrategies.add(featureBucketStrategy);
		return featureBucketStrategy;
	}
}
