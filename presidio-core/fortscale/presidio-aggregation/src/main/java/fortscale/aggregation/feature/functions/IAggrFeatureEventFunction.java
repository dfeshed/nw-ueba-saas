package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.Feature;
import fortscale.common.feature.MultiKeyHistogram;

import java.util.List;
import java.util.Map;

/**
 * @author Amir Ahinoam
 * @author Lior Govrin
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AggrFeatureHistogramFunc.class, name = AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureAvgStdNFunc.class, name = AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureDistinctValuesCounterFunc.class, name = AggrFeatureDistinctValuesCounterFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureSumFunc.class, name = AggrFeatureSumFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventMultiKeyValuesFunc.class, name = AggrFeatureEventMultiKeyValuesFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventNumberOfNewOccurrencesFunc.class, name = AggrFeatureEventNumberOfNewOccurrencesFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventMultiKeyValuesMaxSumFunc.class, name = AggrFeatureEventMultiKeyValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE), @JsonSubTypes.Type(value = AggrFeatureMultiKeyValuesToMaxMaxFunc.class, name = AggrFeatureMultiKeyValuesToMaxMaxFunc.AGGR_FEATURE_FUNCTION_TYPE)
})
public interface IAggrFeatureEventFunction {
    /**
     * Create new feature by running the associated {@link IAggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);

    default MultiKeyHistogram calculateContributionRatios(AggregatedFeatureEventConf aggregatedFeatureEventConf,
                                                          FeatureBucket featureBucket) {
        throw new UnsupportedOperationException(String.format("'Calculate contribution ratios' is not supported by " +
                "%s.", getClass().getSimpleName()));
    }
}
