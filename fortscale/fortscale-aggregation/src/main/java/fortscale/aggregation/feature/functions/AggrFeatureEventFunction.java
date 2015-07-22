package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.aggregation.feature.Feature;
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
    @JsonSubTypes.Type(value = AggrFeatureEventNumberOfDistinctValuesFunc.class, name = AggrFeatureEventNumberOfDistinctValuesFunc.AGGR_FEATURE_FUNCTION_TYPE)
})


public interface AggrFeatureEventFunction {

    /**
     * Create new feature by running the associated {@link AggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     * @param aggrFeatureEventConf the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);
}
