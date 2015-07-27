package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;

import fortscale.utils.ConversionUtils;


public class FixedDurationFeatureBucketStrategyFactory implements FeatureBucketStrategyFactory{
	public static final String	STRATEGY_TYPE = "fixed_duration";
	private static final String	JSON_CONF_DURATION_IN_SECONDS_CONFS_FIELD_NAME = "durationInSeconds";

	@Override
	public FeatureBucketStrategy createFeatureBucketStrategy(StrategyJson strategyJson) throws JsonMappingException {
		Long durationInSeconds = ConversionUtils.convertToLong(strategyJson.getParams().get(JSON_CONF_DURATION_IN_SECONDS_CONFS_FIELD_NAME));
		if(durationInSeconds == null){
			throw new JsonMappingException(String.format("json object %s doesn't contain field %s", strategyJson.getParams().toJSONString(), JSON_CONF_DURATION_IN_SECONDS_CONFS_FIELD_NAME));
		}
		return new FixedDurationFeatureBucketStrategy(strategyJson.getName(), durationInSeconds);
	}

}
