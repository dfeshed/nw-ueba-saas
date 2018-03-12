package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;

import java.util.Map;

/**
 * Aggregate one or more buckets containing a feature containing a mapping from features group to max value.
 * Such a mapping (of type Map<String, Double>) is created by AggrFeatureFeatureToMaxMapFunc.
 * First {@link AbstractAggrFeatureEventFeatureToMaxMapFunc} is used in order to aggregate multiple buckets
 * (refer to its documentation to learn more).
 * Then, all of the values are summed up in order to create a new aggregated feature.
 *
 * Example:
 *    Suppose a user accesses several machines many times, and each machine access gets some score.
 *    This class can be used in order to know the sum of the maximal score each machine got.
 *
 * Parameters this class gets from the ASL:
 * 1. pick: refer to {@link AbstractAggrFeatureEventFeatureToMaxMapFunc}'s documentation to learn more.
 */
@JsonTypeName(AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public class AggrFeatureEventMapValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxMapFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_map_values_max_sum_func";

    @Override
    protected AggrFeatureValue calculateFeaturesGroupToMaxValue(AggrFeatureValue aggrFeatureValue) {
        @SuppressWarnings("unchecked")
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)aggrFeatureValue.getValue();
        double sum = 0;

        for (double max : featuresGroupToMax.values()) {
            sum += max;
        }

        return new AggrFeatureValue(sum, aggrFeatureValue.getTotal());
    }
}
