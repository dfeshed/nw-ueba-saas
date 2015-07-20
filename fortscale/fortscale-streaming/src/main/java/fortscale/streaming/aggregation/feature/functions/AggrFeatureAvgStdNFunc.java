package fortscale.streaming.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;

@JsonTypeName(AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureAvgStdNFunc implements AggrFeatureFunction, AggrFeatureEventFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_av_std_n_func";
    final static String COUNT_BY_FIELD_NAME = "countBy";

    /**
     * Updates the Average, Standard Deviation and Total feature count (N) within the aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     *
     * @param aggregatedFeatureConf aggregated feature configuration.
     * @param features              the values of the relevant features must be of type {@link Double}. Values which are not of type {@link Double} are ignored.
     * @param aggrFeature           the aggregated feature to update. The aggrFeature's value must be of type {@link ContinuousValueAvgStdN}.
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null or aggrFeature is null or
     * if it's value is not of type {@link ContinuousValueAvgStdN}
     */
    @Override
    public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) {
            return null;
        }

        Object value = aggrFeature.getValue();
        if (value == null) {
            value = new ContinuousValueAvgStdN();
            aggrFeature.setValue(value);
        } else if (!(value instanceof ContinuousValueAvgStdN)) {
            throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s",
                aggrFeature.getName(), ContinuousValueAvgStdN.class.getSimpleName()));
        }

        ContinuousValueAvgStdN avgStdN = (ContinuousValueAvgStdN)value;
        if (features != null) {
            List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(COUNT_BY_FIELD_NAME);
            for (String featureName : featureNames) {
                Feature feature = features.get(featureName);
                if (feature != null) {
                    addValue(avgStdN, feature.getValue());
                }
            }
        }

        return avgStdN;
    }

    /**
     * Create new feature by running the associated {@link AggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
            return null;
        }

        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        Feature resFeature = new Feature(aggrFeatureEventConf.getName(), avgStdN);

        List<String> aggregatedFeatureNamesList = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(COUNT_BY_FIELD_NAME);
        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            for (String aggregatedFeatureName : aggregatedFeatureNamesList) {
                Feature aggrFeature = aggrFeatures.get(aggregatedFeatureName);
                if (aggrFeature != null) {
                    if(aggrFeature.getValue() instanceof ContinuousValueAvgStdN) {
                        avgStdN.add((ContinuousValueAvgStdN) aggrFeature.getValue());
                    } else {
                        throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s",
                                aggregatedFeatureName, ContinuousValueAvgStdN.class.getSimpleName()));
                    }
                }
            }
        }

        return resFeature;
    }

    private void addValue(ContinuousValueAvgStdN avgStdN, Object value) {
        try {
            Double doubleValue = (Double)value;
            avgStdN.add(doubleValue);
        } catch (ClassCastException e) {
            // Value ignored
        }
    }
}
