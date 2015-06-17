package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 16/06/2015.
 */
public interface IAggrFeatureFunctionsService {
    Map<String, Feature> updateAggrFeatures(List<AggregatedFeatureConf> aggrFeatureConfs, Map<String, Feature>aggrFeatures, Map<String, Feature>features);
}
