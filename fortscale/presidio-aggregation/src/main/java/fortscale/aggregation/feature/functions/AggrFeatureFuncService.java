package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.Feature;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */
public class AggrFeatureFuncService implements IAggrFeatureFunctionsService, IAggrFeatureEventFunctionsService {
    private static final Logger logger = Logger.getLogger(AggrFeatureFuncService.class);

    private Map<JSONObject, IAggrFeatureFunction> aggrFunctions = new HashMap<>();
    private Map<JSONObject, IAggrFeatureEventFunction> aggrFeatureEventFunctions = new HashMap<>();

    /**
     * Updates the aggrFeatures by running the associated {@link IAggrFeatureFunction} that is configured for each
     * AggrFeature in the given  {@link AggregatedFeatureConf} and using the features as input to those functions.
     * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
     * map.
     * @param aggrFeatureConfs
     * @param aggrFeatures
     * @param features
     * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
     * If aggrFeatures is null, a new {@link HashMap<String, Feature>} will be created with new Feature object for each
     * of the {@link AggregatedFeatureConf} in aggrFeatureConfs.
     */
    @Override
    public Map<String, Feature> updateAggrFeatures(JSONObject jSONObject,
                                                   List<AggregatedFeatureConf> aggrFeatureConfs,
                                                   Map<String, Feature> aggrFeatures,
                                                   Map<String, Feature> features) {
        if(aggrFeatures==null) {
            aggrFeatures = new HashMap<String, Feature>();
        }

        if(aggrFeatureConfs==null) {
            logger.warn("updateAggrFeatures(): No AggregatedFeatureConf was provided");
        } else {
            for (AggregatedFeatureConf aggregatedFeatureConf: aggrFeatureConfs) {
                if (!aggregatedFeatureConf.passedFilter(jSONObject)) {
                    continue;
                }
                String aggrFeatureName = aggregatedFeatureConf.getName();
                Feature aggrFeature = aggrFeatures.get(aggrFeatureName);
                if(aggrFeature==null) {
                    aggrFeature = new Feature(aggrFeatureName);
                    aggrFeatures.put(aggrFeatureName, aggrFeature);
                }

                IAggrFeatureFunction func = getAggrFeatureFunction(aggregatedFeatureConf);
                func.updateAggrFeature(aggregatedFeatureConf, features, aggrFeature);
            }
        }
        return aggrFeatures;
    }

    /**
     * Create new feature by running the associated {@link IAggrFeatureFunction} that is configured in the given
     * {@link AggregatedFeatureConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf                    the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        Feature res = null;
        if(multipleBucketsAggrFeaturesMapList==null) {
            logger.warn("calculateAggrFeature(): multipleBucketsAggrFeaturesMapList is null");
        } else if(aggrFeatureEventConf==null) {
            logger.warn("calculateAggrFeature(): aggrFeatureEventConf is null");
        } else {
            IAggrFeatureEventFunction func = getAggrFeatureEventFunction(aggrFeatureEventConf);
            if (func != null) {
                res = func.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
            }
        }
        return res;
    }

    private IAggrFeatureEventFunction getAggrFeatureEventFunction(AggregatedFeatureEventConf aggrFeatureEventConf) {
        JSONObject funcAsJsonObject = aggrFeatureEventConf.getAggregatedFeatureEventFunction();
        String funcAsJsonString = funcAsJsonObject.toJSONString();
        IAggrFeatureEventFunction func = aggrFeatureEventFunctions.get(funcAsJsonObject);
        if (func == null) {
            try {
                func = (new ObjectMapper()).readValue(funcAsJsonString, IAggrFeatureEventFunction.class);
                aggrFeatureEventFunctions.put(funcAsJsonObject, func);
            } catch (Exception e) {
                logger.error(String.format("Failed to deserialize function JSON %s", funcAsJsonString), e);
            }
        }
        return func;
    }

    private IAggrFeatureFunction getAggrFeatureFunction(AggregatedFeatureConf aggregatedFeatureConf) {

        IAggrFeatureFunction func = aggrFunctions.get(aggregatedFeatureConf.getAggrFeatureFuncJson());

        if(func==null) {
            func = createAggrFeatureFunction(aggregatedFeatureConf);
        }
        return func;
    }

    private IAggrFeatureFunction createAggrFeatureFunction(AggregatedFeatureConf aggregatedFeatureConf) {
        JSONObject aggrFeatureFuncJson = aggregatedFeatureConf.getAggrFeatureFuncJson();
        String aggrFeatureFuncString = aggrFeatureFuncJson.toJSONString();
        IAggrFeatureFunction func = null;

        try {
            func = (new ObjectMapper()).readValue(aggrFeatureFuncString, IAggrFeatureFunction.class);
            aggrFunctions.put(aggrFeatureFuncJson, func);
        } catch (Exception e) {
            logger.error(String.format("Failed to deserialize function JSON %s", aggrFeatureFuncString), e);
        }
        return func;
    }

    @Override
    public int getNumberOfAggrFeatureEventFunctions() {
        return aggrFeatureEventFunctions.size();
    }

    @Override
    public int getNumberOfAggrFeatureFunctions() {
        return aggrFunctions.size();
    }
}
