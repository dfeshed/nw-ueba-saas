package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.Feature;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Amir Ahinoam
 */
public class AggrFeatureFuncService implements IAggrFeatureFunctionsService, IAggrFeatureEventFunctionsService {
    private static final Logger logger = Logger.getLogger(AggrFeatureFuncService.class);
    private static final ObjectMapper objectMapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();

    private Map<JSONObject, IAggrFeatureFunction> aggrFunctions = new HashMap<>();
    private Map<JSONObject, IAggrFeatureEventFunction> aggrFeatureEventFunctions = new HashMap<>();

    /**
     * Updates the aggrFeatures by running the associated {@link IAggrFeatureFunction} that is configured for each
     * AggrFeature in the given {@link AggregatedFeatureConf} and using the features as input to those functions.
     * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
     * map.
     *
     * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
     * If aggrFeatures is null, a new map <String, Feature> will be created with new Feature object for each of the
     * {@link AggregatedFeatureConf} in aggrFeatureConfs.
     */
    @Override
    public Map<String, Feature> updateAggrFeatures(
            AdeRecordReader adeRecordReader,
            List<AggregatedFeatureConf> aggrFeatureConfs,
            Map<String, Feature> aggrFeatures,
            Map<String, Feature> features) {

        if (aggrFeatures == null) {
            aggrFeatures = new HashMap<>();
        }

        if (aggrFeatureConfs == null) {
            logger.warn("updateAggrFeatures(): No AggregatedFeatureConf was provided");
        } else {
            for (AggregatedFeatureConf aggregatedFeatureConf : aggrFeatureConfs) {
                if (!aggregatedFeatureConf.passedFilter(adeRecordReader)) continue;
                String aggrFeatureName = aggregatedFeatureConf.getName();
                Feature aggrFeature = aggrFeatures.get(aggrFeatureName);

                if (aggrFeature == null) {
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
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function
     */
    @Override
    public Feature calculateAggrFeature(
            AggregatedFeatureEventConf aggrFeatureEventConf,
            List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {

        Feature res = null;

        if (multipleBucketsAggrFeaturesMapList == null) {
            logger.warn("calculateAggrFeature(): multipleBucketsAggrFeaturesMapList is null");
        } else if (aggrFeatureEventConf == null) {
            logger.warn("calculateAggrFeature(): aggrFeatureEventConf is null");
        } else {
            IAggrFeatureEventFunction func = getAggrFeatureEventFunction(aggrFeatureEventConf);
            if (func != null) res = func.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        }

        return res;
    }

    private IAggrFeatureEventFunction getAggrFeatureEventFunction(
            AggregatedFeatureEventConf aggregatedFeatureEventConf) {

        JSONObject jsonObject = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction();
        IAggrFeatureEventFunction aggrFeatureEventFunction = aggrFeatureEventFunctions.get(jsonObject);

        if (aggrFeatureEventFunction == null) {
            aggrFeatureEventFunction = deserializeAggrFeatureEventFunction(jsonObject);
            aggrFeatureEventFunctions.put(jsonObject, aggrFeatureEventFunction);
        }

        return aggrFeatureEventFunction;
    }

    public static IAggrFeatureEventFunction deserializeAggrFeatureEventFunction(
            JSONObject aggregatedFeatureEventFunction) {

        String jsonString = aggregatedFeatureEventFunction.toJSONString();

        try {
            return objectMapper.readValue(jsonString, IAggrFeatureEventFunction.class);
        } catch (Exception e) {
            String msg = String.format("Failed to deserialize aggregated feature event function %s.", jsonString);
            logger.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private IAggrFeatureFunction getAggrFeatureFunction(AggregatedFeatureConf aggregatedFeatureConf) {
        IAggrFeatureFunction func = aggrFunctions.get(aggregatedFeatureConf.getAggrFeatureFuncJson());
        if (func == null) func = createAggrFeatureFunction(aggregatedFeatureConf);
        return func;
    }

    private IAggrFeatureFunction createAggrFeatureFunction(AggregatedFeatureConf aggregatedFeatureConf) {
        JSONObject aggrFeatureFuncJson = aggregatedFeatureConf.getAggrFeatureFuncJson();
        String aggrFeatureFuncString = aggrFeatureFuncJson.toJSONString();
        IAggrFeatureFunction func = null;

        try {
            func = objectMapper.readValue(aggrFeatureFuncString, IAggrFeatureFunction.class);
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
