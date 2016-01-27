package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;

import java.util.Map;

/**
 * Created by amira on 16/06/2015.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = AggrFeatureHistogramFunc.class, name = AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE),
		@JsonSubTypes.Type(value = AggrFeatureAvgStdNFunc.class, name = AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE),
		@JsonSubTypes.Type(value = AggrFeatureSumFunc.class, name = AggrFeatureSumFunc.AGGR_FEATURE_FUNCTION_TYPE),
		@JsonSubTypes.Type(value = AggrFeatureFeatureToMaxMapFunc.class, name = AggrFeatureFeatureToMaxMapFunc.AGGR_FEATURE_FUNCTION_TYPE)
})

public interface IAggrFeatureFunction {

    /**
     * Updates the value within aggrFeature by applying the implemented function on aggrFeature value.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     * @param aggregatedFeatureConf
     * @param features the values of the relevant features must be of the expected type by the implementation.
     *                 Values which are not of the expected type are ignored.
     * @param aggrFeature the aggregated feature to update. The aggrFeature's value must be of the expected type
     *                    by the underlying implementation.
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null or aggrFeature is null
     * or it's value is not of the expected type.
     */
    FeatureValue updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature);
}
