package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.AggrFeatureFunctionUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


@JsonTypeName(AggrFeatureMultiKeyHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureMultiKeyHistogramFunc implements IAggrFeatureFunction, IAggrFeatureEventFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_multi_key_histogram_func";
    public final static String GROUP_BY_FIELD_NAME = "groupBy";

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
    public FeatureValue updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
        if (aggregatedFeatureConf == null || aggrFeature == null) {
            return null;
        }

        FeatureValue value = aggrFeature.getValue();
        if (value == null) {
            value = new MultiKeyHistogram();
            aggrFeature.setValue(value);
        } else if (!(value instanceof MultiKeyHistogram)) {
            throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s",
                    aggrFeature.getName(), GenericHistogram.class.getSimpleName()));
        }

        MultiKeyHistogram multiKeyHistogram = (MultiKeyHistogram) value;
        if (features != null) {
            List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
            MultiKeyFeature multiKeyFeature = AggrFeatureFunctionUtils.extractGroupByFeatureValues(features, featureNames);

            if (multiKeyFeature != null) {
                multiKeyHistogram.add(multiKeyFeature, 1.0);
            }
        }

        return multiKeyHistogram;
    }

    /**
     * Create new feature by running the associated {@link IAggrFeatureFunction} that is configured in the given
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

        MultiKeyHistogram histogram = calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList, false, Collections.emptyList());
        return new Feature(aggrFeatureEventConf.getName(), histogram);
    }

    public static MultiKeyHistogram calculateHistogramFromBucketAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList,
                                                                            boolean removeNA, List<String> additionalNAValues) {
        MultiKeyHistogram histogram = new MultiKeyHistogram();

        List<String> aggregatedFeatureNamesList = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
        Assert.notNull(aggregatedFeatureNamesList, "groupBy is required; it must not be null");
        for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            for (String aggregatedFeatureName : aggregatedFeatureNamesList) {
                Feature aggrFeature = aggrFeatures.get(aggregatedFeatureName);
                if (aggrFeature != null) {
                    if (aggrFeature.getValue() instanceof MultiKeyHistogram) {
                        if (removeNA) {
                            Set<String> filter = AggGenericNAFeatureValues.getNAValues();
                            filter.addAll(additionalNAValues);
                            histogram.add((MultiKeyHistogram) aggrFeature.getValue(), filter.stream().map(FeatureStringValue::new).collect(Collectors.toSet()));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s",
                                aggregatedFeatureName, GenericHistogram.class.getSimpleName()));
                    }
                }
            }
        }

        return histogram;
    }
}
