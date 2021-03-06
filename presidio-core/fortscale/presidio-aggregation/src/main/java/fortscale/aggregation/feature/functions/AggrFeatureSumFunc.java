package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeName(AggrFeatureSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureSumFunc implements IAggrFeatureFunction, IAggrFeatureEventFunction {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_sum_func";
    private static final String SUM_FIELD_NAME = "sum";

    /**
     * Updates the aggrFeatures by running the associated {@link IAggrFeatureFunction} that is configured for each
     * AggrFeature in the given  {@link AggregatedFeatureConf} and using the features as input to those functions.
     * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
     * map.
     *
     * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
     * If aggrFeatures is null, a new {@link HashMap <String, Feature>} will be created with new Feature object for each
     * of the {@link AggregatedFeatureConf} in aggrFeatureConfs.
     */
    @Override
    public FeatureValue updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) return null;
        FeatureValue value = aggrFeature.getValue();

        if (value == null) {
            value = new AggrFeatureValue(0D);
            aggrFeature.setValue(value);
        } else if (!(value instanceof AggrFeatureValue && ((AggrFeatureValue)value).getValue() instanceof Double)) {
            throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s containing %s",
                    aggrFeature.getName(), AggrFeatureValue.class.getSimpleName(), Double.class.getSimpleName()));
        }

        AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)value;
        Double oldSum = (Double)aggrFeatureValue.getValue();
        double addend = getFeatureValueToSum(aggregatedFeatureConf, features);
        aggrFeatureValue.setValue(oldSum + addend);
        return aggrFeatureValue;
    }

    private double getFeatureValueToSum(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features) {
        String featureNameToSum = getFeatureNameToSum(aggregatedFeatureConf.getFeatureNamesMap(), true);
        double addend;

        if (featureNameToSum == null) {
            addend = 1;
        } else {
            Feature featureToSum = features.get(featureNameToSum);
            addend = ((FeatureNumericValue)featureToSum.getValue()).getValue().doubleValue();
        }

        return addend;
    }

    /**
     * Create new feature by running the associated {@link IAggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) return null;
        double sum = 0;
        String featureNameToSum = getFeatureNameToSum(aggrFeatureEventConf.getAggregatedFeatureNamesMap(), false);

        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            Feature featureToSum = aggrFeatures.get(featureNameToSum);

            if (featureToSum != null) {
                if (!(featureToSum.getValue() instanceof AggrFeatureValue && ((AggrFeatureValue)featureToSum.getValue()).getValue() instanceof Double)) {
                    throw new IllegalArgumentException(String.format("Missing aggregated feature named %s containing %s with %s",
                            featureNameToSum, AggrFeatureValue.class.getSimpleName(), Double.class.getSimpleName()));
                }

                sum += (Double)((AggrFeatureValue)featureToSum.getValue()).getValue();
            }
        }

        return new Feature(aggrFeatureEventConf.getName(), new AggrFeatureValue(sum));
    }

    private static String getFeatureNameToSum(Map<String, List<String>> aggregatedFeatureNamesMap, boolean forgiving) {
        List<String> aggregatedFeatureNamesList = aggregatedFeatureNamesMap.get(SUM_FIELD_NAME);

        if (aggregatedFeatureNamesList == null) {
            aggregatedFeatureNamesList = Collections.emptyList();
        }

        if (aggregatedFeatureNamesList.size() > 1 || (!forgiving && aggregatedFeatureNamesList.size() != 1)) {
            throw new IllegalArgumentException(String.format("wrong number of parameters: %d", aggregatedFeatureNamesList.size()));
        }

        if (aggregatedFeatureNamesList.isEmpty()) {
            return null;
        }

        return aggregatedFeatureNamesList.get(0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AggrFeatureSumFunc;
    }
}
