package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.logging.Logger;
import org.eclipse.jdt.internal.core.Assert;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */

@Service
public class AggrFeatureFuncService implements IAggrFeatureFunctionsService, IAggrFeatureEventFunctionsService {
    private static final Logger logger = Logger.getLogger(AggrFeatureFuncService.class);

    private Map<String, AggrFeatureFunction> aggrFunctions = new HashMap<>();
    private Map<String, AggrFeatureEventFunction> aggrFeatureEventFunctions = new HashMap<>();


    /**
     * Updates the aggrFeatures by running the associated {@link AggrFeatureFunction} that is configured for each
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
    public Map<String, Feature> updateAggrFeatures(List<AggregatedFeatureConf> aggrFeatureConfs,
                                                   Map<String, Feature> aggrFeatures,
                                                   Map<String, Feature> features) {
        if(aggrFeatures==null) {
            aggrFeatures = new HashMap<String, Feature>();
        }

        if(aggrFeatureConfs==null) {
            logger.warn("updateAggrFeatures(): No AggregatedFeatureConf was provided");
        } else {
            for (AggregatedFeatureConf aggregatedFeatureConf: aggrFeatureConfs) {
                String aggrFeatureName = aggregatedFeatureConf.getName();
                Feature aggrFeature = aggrFeatures.get(aggrFeatureName);
                if(aggrFeature==null) {
                    aggrFeature = new Feature(aggrFeatureName, null);
                    aggrFeatures.put(aggrFeatureName, aggrFeature);
                }

                AggrFeatureFunction func = getAggrFeatureFunction(aggregatedFeatureConf);
                func.updateAggrFeature(aggregatedFeatureConf, features, aggrFeature);
            }
        }
        return aggrFeatures;
    }

    /**
     * Create new feature by running the associated {@link AggrFeatureFunction} that is configured in the given
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
            AggrFeatureEventFunction func = getAggrFeatureEventFunction(aggrFeatureEventConf);
            res = func.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        }
        return res;
    }

    private AggrFeatureEventFunction getAggrFeatureEventFunction(AggregatedFeatureEventConf aggrFeatureEventConf) {
        Assert.isNotNull(aggrFeatureEventConf);

        AggrFeatureEventFunction func = null;

        try {
            String json = aggrFeatureEventConf.getAggregatedFeatureEventFunction().toJSONString();
            func = (new ObjectMapper()).readValue(json, AggrFeatureEventFunction.class);
            aggrFeatureEventFunctions.put(json, func);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to deserialize json %s", aggrFeatureEventConf.getAggregatedFeatureEventFunction());
            logger.error(errorMsg, e);
        }
        return func;
    }

    private AggrFeatureFunction getAggrFeatureFunction(@NotNull AggregatedFeatureConf aggregatedFeatureConf) {

        AggrFeatureFunction func = aggrFunctions.get(aggregatedFeatureConf.getAggrFeatureFuncJson());

        if(func==null) {
            func = createAggrFeatureFunction(aggregatedFeatureConf);
        }
        return func;
    }

    private AggrFeatureFunction createAggrFeatureFunction(@NotNull AggregatedFeatureConf aggregatedFeatureConf) {
        Assert.isNotNull(aggregatedFeatureConf);

        AggrFeatureFunction func = null;

        try {
            String json = aggregatedFeatureConf.getAggrFeatureFuncJson();
            func = (new ObjectMapper()).readValue(json, AggrFeatureFunction.class);
            aggrFunctions.put(json, func);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to deserialize json %s", aggregatedFeatureConf.getAggrFeatureFuncJson());
            logger.error(errorMsg, e);
        }
        return func;
    }



}
