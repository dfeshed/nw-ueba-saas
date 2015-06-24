package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
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
public class AggrFeatureFuncService implements IAggrFeatureFunctionsService {
    private static final Logger logger = Logger.getLogger(AggrFeatureFuncService.class);

    private Map<String, AggrFeatureFunction> aggrFunctions = new HashMap<>();


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
            for (int i = 0; i < aggrFeatureConfs.size(); i++) {
                AggregatedFeatureConf aggregatedFeatureConf =  aggrFeatureConfs.get(i);
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

    private AggrFeatureFunction getAggrFeatureFunction(@NotNull AggregatedFeatureConf aggregatedFeatureConf) {
        Assert.isNotNull(aggregatedFeatureConf);

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
