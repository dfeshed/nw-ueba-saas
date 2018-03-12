package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Aggregate events into a mapping from a group of features to a maximal number (of type Map<String, Double>).
 * This is best explained using an example:
 * Suppose a user accesses several machines from several machines many times, and each access gets some score.
 * This class can be used in order to know the maximal score each pair of source machine and destination machine got.
 * In this case, the group of features will be the tuple <source machine name, destination machine name>.
 * Each of the values of this tuple will be extracted from the feature values contained in the events.
 * The score (which is to be maximized) is also extracted from the events.
 * Parameters this class gets from the ASL:
 * 1. groupBy: a list containing the names of the features whose values should be used as a key of the resulting map.
 * 2. maximize: the name of the feature whose numeric value should be extracted and maximized.
 */
@JsonTypeName(AggrFeatureFeatureToMaxMapFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureFeatureToMaxMapFunc implements IAggrFeatureFunction {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_feature_to_max_map_func";
    public static final String FEATURE_GROUP_SEPARATOR_KEY = "# # #";
    public static final String FEATURE_SEPARATOR_KEY = "#";
    public static final String GROUP_BY_FIELD_NAME = "groupBy";
    public static final String MAXIMIZE_FIELD_NAME = "maximize";

    /**
     * Updates the mapping from feature value to max value within aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     *
     * @param aggregatedFeatureConf aggregated feature configuration
     * @param features              mapping of feature name to feature value
     * @param aggrFeature           the aggregated feature to update. The aggrFeature's value must be of type Map<String, Double>
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null or aggrFeature is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public FeatureValue updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) {
            return null;
        }

        FeatureValue value = aggrFeature.getValue();

        if (value == null) {
            value = new AggrFeatureValue(new HashMap<>(), 0L);
            aggrFeature.setValue(value);
        } else if (!(value instanceof AggrFeatureValue && ((AggrFeatureValue)value).getValue() instanceof Map)) {
            throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s and contain a %s.",
                    aggrFeature.getName(), AggrFeatureValue.class.getSimpleName(), Map.class.getSimpleName()));
        }

        AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)value;
        Map<String, Double> featureToMaxMap = (Map<String, Double>)aggrFeatureValue.getValue();

        if (features != null) {
            List<String> groupByFeatureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
            String maximizeFeatureName = aggregatedFeatureConf.getFeatureNamesMap().get(MAXIMIZE_FIELD_NAME).get(0);
            String groupByFeatureValues = extractGroupByFeatureValues(features, groupByFeatureNames);
            Feature maximizeFeatureValue = features.get(maximizeFeatureName);

            if (groupByFeatureValues != null && maximizeFeatureValue != null && maximizeFeatureValue.getValue() != null) {
                Double max = featureToMaxMap.get(groupByFeatureValues);
                double potentialMax = ((FeatureNumericValue)maximizeFeatureValue.getValue()).getValue().doubleValue();
                featureToMaxMap.put(groupByFeatureValues, max == null ? potentialMax : Math.max(max, potentialMax));
                aggrFeatureValue.setTotal(aggrFeatureValue.getTotal() + 1);
            }
        }

        return value;
    }

    private String extractGroupByFeatureValues(Map<String, Feature> features, List<String> groupByFeatureNames) {
        if (groupByFeatureNames == null) return StringUtils.EMPTY;
        List<String> groupByFeatureNamesAndValues = new ArrayList<>(groupByFeatureNames.size());

        for (String groupByFeatureName : groupByFeatureNames) {
            Feature groupByFeatureValue = features.get(groupByFeatureName);
            if (groupByFeatureValue == null || groupByFeatureValue.getValue() == null) return null;
            groupByFeatureNamesAndValues.add(groupByFeatureName + FEATURE_SEPARATOR_KEY + groupByFeatureValue.getValue().toString());
        }

        return String.join(FEATURE_GROUP_SEPARATOR_KEY, groupByFeatureNamesAndValues);
    }
}
