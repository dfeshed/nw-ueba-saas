package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyHistogram;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.AggrFeatureFunctionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Aggregate one or more buckets containing a feature containing a mapping from features group to max value.
 * Such a mapping (of type MultiKeyHistogram) is created by AggrFeatureMultiKeyToMaxFunc.
 * First {@link AbstractAggrFeatureEventFeatureToMaxFunc} is used in order to aggregate multiple buckets
 * (refer to its documentation to learn more).
 * Then, all of the values or filtered values by keys are summed up in order to create a new aggregated feature.
 * <p>
 * Example:
 * Suppose a user accesses several machines many times, and each machine access gets some score.
 * This class can be used in order to know the sum of the maximal score each machine got.
 * <p>
 * Parameters this class gets from the ASL:
 * 1. pick: refer to {@link AbstractAggrFeatureEventFeatureToMaxFunc}'s documentation to learn more.
 */
@JsonTypeName(AggrFeatureEventMultiKeyValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public class AggrFeatureEventMultiKeyValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_multi_key_values_max_sum_func";
    public static final String KEY_FIELD_NAME = "keys";
    private Set<MultiKeyFeature> keys;

    public AggrFeatureEventMultiKeyValuesMaxSumFunc() {
        this.keys = new HashSet<>();
    }

    @JsonProperty(KEY_FIELD_NAME)
    public void setKeys(Set<Map<String, String>> keys) {
        keys.forEach(features -> {
            MultiKeyFeature featureNamesAndValues = AggrFeatureFunctionUtils.buildMultiKeyFeature(features);
            this.keys.add(featureNamesAndValues);
        });
    }

    @Override
    protected AggrFeatureValue calculateFeaturesGroupToMaxValue(MultiKeyHistogram multiKeyHistogram) {
        @SuppressWarnings("unchecked")
        double sum = 0;

        Map<MultiKeyFeature, Double> histogram = multiKeyHistogram.getHistogram();

        //sum all if no keys were defined
        if (keys.isEmpty()) {
            sum = histogram.values().stream().mapToDouble(Double::doubleValue).sum();
        } else {
            //sum all max values of histogram, whose contain one of the keys (e.g: operationType=FILE_OPENED)
            for (Map.Entry<MultiKeyFeature, Double> multiKeyRecordEntry : histogram.entrySet()) {
                for (MultiKeyFeature key : keys) {
                    if (multiKeyRecordEntry.getKey().contains(key.getFeatureNameToValue())) {
                        Double max = multiKeyRecordEntry.getValue();
                        sum += max;
                        //todo: recalculate total?
                        break;
                    }
                }
            }
        }

        return new AggrFeatureValue(sum, (long) multiKeyHistogram.getTotal());
    }
}
