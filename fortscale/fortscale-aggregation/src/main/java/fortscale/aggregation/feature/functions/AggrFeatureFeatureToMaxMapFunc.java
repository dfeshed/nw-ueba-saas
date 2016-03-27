package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * Aggregate events into a mapping from a group of features to a maximal number (of type Map<List<String>, Integer>).
 * This is best explained using an example:
 * Suppose a user accesses several machines from several machines many times, and each access gets some score.
 * This class can be used in order to know the maximal score each pair of source machine and destination machine got.
 * In this case, the features group will be the tuple (source machine's name, destination machine's name).
 * Each of the values of this tuple will be extracted from the features' values contained in the aggregated events.
 * The score (which is to be maximized) is also extracted from the events's features.
 *
 * Parameters this class gets from the ASL:
 * 1. groupBy: a list containing the names of the features whose values should be used as a key of the resulting map.
 * 2. maximize: the name of the feature whose numeric value should be extracted and maximized.
 */
@JsonTypeName(AggrFeatureFeatureToMaxMapFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureFeatureToMaxMapFunc implements IAggrFeatureFunction {
    protected static final String FEATURE_GROUP_SEPERATOR_KEY = "# # #";
    protected static final String FEATURE_SEPERATOR_KEY = "#";
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_feature_to_max_map_func";
    public final static String GROUP_BY_FIELD_NAME = "groupBy";
    public final static String MAXIMIZE_FIELD_NAME = "maximize";

    /**
     * Updates the mapping from feature value to max value within aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     *
     * @param aggregatedFeatureConf aggregated feature configuration
     * @param features              mapping of feature name to feature
     * @param aggrFeature           the aggregated feature to update. The aggrFeature's value must be of type {@link Map<List<String>, Integer>}
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null or aggrFeature is null
     */
    @Override
    public FeatureValue updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) {
            return null;
        }

        FeatureValue value = aggrFeature.getValue();
        if (value == null) {
            Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
            value = new AggrFeatureValue(featuresGroupToMax, 0L);
            aggrFeature.setValue(value);
        } else if (!(value instanceof AggrFeatureValue && ((AggrFeatureValue) value).getValue() instanceof Map)) {
            throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s",
                    aggrFeature.getName(), Map.class.getSimpleName()));
        }

        AggrFeatureValue aggFeatureValue = (AggrFeatureValue) value;
        Map<String, Integer> featuresGroupToMax = (Map<String, Integer>) aggFeatureValue.getValue();
        if (features != null) {
            List<String> groupByFeatureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
            String maximizeFeatureName = aggregatedFeatureConf.getFeatureNamesMap().get(MAXIMIZE_FIELD_NAME).get(0);
            String groupByFeatureValues = extractGroupByFeatureValues(features, groupByFeatureNames);
            Feature featureToMaximize = features.get(maximizeFeatureName);
            if (groupByFeatureValues != null && featureToMaximize != null && featureToMaximize.getValue() != null) {
                Integer max = featuresGroupToMax.get(groupByFeatureValues);
                if (max == null) {
                    max = Integer.MIN_VALUE;
                }
                int num = ((FeatureNumericValue) featureToMaximize.getValue()).getValue().intValue();
                featuresGroupToMax.put(groupByFeatureValues, Math.max(max, num));
                aggFeatureValue.setTotal(aggFeatureValue.getTotal() + 1);
            }
        }

        return value;
    }

    private String extractGroupByFeatureValues(Map<String, Feature> features, List<String> groupByFeatureNames) {
        if (groupByFeatureNames == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String groupByFeatureName : groupByFeatureNames) {
            Feature featureToGroupBy = features.get(groupByFeatureName);
            if (featureToGroupBy == null || featureToGroupBy.getValue() == null) {
                return null;
            }
            if(builder.length() > 0){
                builder.append(FEATURE_GROUP_SEPERATOR_KEY);
            }
            builder.append(groupByFeatureName).append(FEATURE_SEPERATOR_KEY).append(((FeatureStringValue) featureToGroupBy.getValue()).getValue());
        }
        return builder.toString();
    }
}
