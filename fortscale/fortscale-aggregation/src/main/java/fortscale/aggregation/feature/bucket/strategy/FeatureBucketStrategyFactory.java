package fortscale.aggregation.feature.bucket.strategy;

import com.fasterxml.jackson.databind.JsonMappingException;


public interface FeatureBucketStrategyFactory {

	public FeatureBucketStrategy createFeatureBucketStrategy(StrategyJson strategyJson) throws JsonMappingException;
}
