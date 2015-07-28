package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

/**
 * Created by amira on 16/06/2015.
 */
public interface IAggrFeatureFunctionsService {
    /**
     * Updates the aggrFeatures by running the associated {@link AggrFeatureFunction} that is configured for each
     * AggrFeature in the given  {@link AggregatedFeatureConf} and using the features as input to those functions.
     * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
     * map.
     * @param aggrFeatureConfs
     * @param aggrFeatures
     * @param features
     * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
     * If aggrFeatures is null, a new {@link HashMap <String, Feature>} will be created with new Feature object for each
     * of the {@link AggregatedFeatureConf} in aggrFeatureConfs.
     */
    Map<String, Feature> updateAggrFeatures(JSONObject event, List<AggregatedFeatureConf> aggrFeatureConfs, Map<String, Feature>aggrFeatures, Map<String, Feature>features);

    /**
     * Returns the number of functions created and stored by this service.
     * This method is mainly used in unit tests.
     *
     * @return the number of functions.
     */
    public int getNumberOfAggrFeatureFunctions();
}
