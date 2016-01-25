package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 16/06/2015.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AggrFeatureHistogramFunc.class, name = AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureAvgStdNFunc.class, name = AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventHistogramMaxCountObjectFunc.class, name = AggrFeatureEventHistogramMaxCountObjectFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureDistinctValuesCounterFunc.class, name = AggrFeatureDistinctValuesCounterFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureSumFunc.class, name = AggrFeatureSumFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventNumberOfNewOccurencesFunc.class, name = AggrFeatureEventNumberOfNewOccurencesFunc.AGGR_FEATURE_FUNCTION_TYPE),
    @JsonSubTypes.Type(value = AggrFeatureEventMapValuesMaxSumFunc.class, name = AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
})


public interface IAggrFeatureEventFunction {
	public static final String AGGR_FEATURE_EVENT_VALUE_FIELD_NAME = "value";
	public static final String AGGR_FEATURE_EVENT_ADDITIONAL_INFO_FIELD_NAME = "additionalInformationMap";

    /**
     * Create new feature by running the associated {@link IAggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     * @param aggrFeatureEventConf the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);
}
