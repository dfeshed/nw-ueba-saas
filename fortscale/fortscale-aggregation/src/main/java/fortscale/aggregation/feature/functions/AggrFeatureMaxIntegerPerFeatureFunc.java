package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.FeatureNumericValue;
import fortscale.aggregation.feature.FeatureStringValue;
import fortscale.aggregation.feature.FeatureValue;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonTypeName(AggrFeatureMaxIntegerPerFeatureFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureMaxIntegerPerFeatureFunc implements IAggrFeatureFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_max_integer_per_feature_func";
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

        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();
        if (features != null) {
            List<String> groupByFeatureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
            String maximizeFeatureName = aggregatedFeatureConf.getFeatureNamesMap().get(MAXIMIZE_FIELD_NAME).get(0);
            List<String> groupByFeatureValues = extractGroupByFeatureValues(features, groupByFeatureNames);
            Feature featureToMaximize = features.get(maximizeFeatureName);
            if (groupByFeatureValues != null && featureToMaximize != null) {
                Integer max = featuresGroupToMax.get(groupByFeatureValues);
                if (max == null) {
                    max = Integer.MIN_VALUE;
                }
                int num = ((FeatureNumericValue) featureToMaximize.getValue()).getValue().intValue();
                featuresGroupToMax.put(groupByFeatureValues, Math.max(max, num));
            }
        }

        return value;
    }

    private List<String> extractGroupByFeatureValues(Map<String, Feature> features, List<String> groupByFeatureNames) {
        List<String> groupByFeatureValues = new ArrayList<>(groupByFeatureNames.size());
        for (String groupByFeatureName : groupByFeatureNames) {
			Feature featureToGroupBy = features.get(groupByFeatureName);
            if (featureToGroupBy == null) {
                return null;
            }
            groupByFeatureValues.add(((FeatureStringValue) featureToGroupBy.getValue()).getValue());
		}
        return groupByFeatureValues;
    }
}
