package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.List;
import java.util.Map;

public class AggrFeatureHistogramFunc implements AggrFeatureFunction, AggrFeatureEventFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_func";
    final static String GROUP_BY_FIELD_NAME = "groupBy";

    /**
     * Updates the histogram within aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     *
     * @param aggregatedFeatureConf aggregated feature configuration
     * @param features              mapping of feature name to feature
     * @param aggrFeature           the aggregated feature to update. The aggrFeature's value must be of type {@link GenericHistogram}
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null  or aggrFeature is null or
     * if it's value is not of type {@link GenericHistogram}
     */
    @Override
    public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) {
            return null;
        }

        Object value = aggrFeature.getValue();
        if (value == null) {
            value = new GenericHistogram();
            aggrFeature.setValue(value);
        } else if (!(value instanceof GenericHistogram)) {
            return null;
            // TODO: throw exception instead?
        }

        GenericHistogram histogram = (GenericHistogram)value;
        if (features != null) {
            List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
            for (String featureName : featureNames) {
                Feature feature = features.get(featureName);
                if (feature != null) {
                    histogram.add(feature.getValue(), 1.0);
                }
            }
        }

        return histogram;
    }

    /**
     * Create new feature by running the associated {@link AggrFeatureFunction} that is configured in the given
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

        GenericHistogram histogram = new GenericHistogram();
        Feature resFeature = new Feature(aggrFeatureEventConf.getName(), histogram);

        List<String> aggregatedFeatureNamesList = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            for (String featureName : aggregatedFeatureNamesList) {
                Feature aggrFeature = aggrFeatures.get(featureName);
                if (aggrFeature != null && aggrFeature.getValue() instanceof GenericHistogram) {
                    histogram.add((GenericHistogram)aggrFeature.getValue());
                } else {
                    return null;
                    // TODO: throw exception instead?
                }
            }
        }

        return resFeature;
    }
}
