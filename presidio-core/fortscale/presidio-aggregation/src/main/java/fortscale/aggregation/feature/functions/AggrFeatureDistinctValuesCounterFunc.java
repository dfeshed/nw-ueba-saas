package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import fortscale.utils.AggrFeatureFunctionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Amir Ahinoam
 * @author Lior Govrin
 */
@JsonTypeName(AggrFeatureDistinctValuesCounterFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureDistinctValuesCounterFunc extends AbstractAggrFeatureEventHistogram {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_distinct_multi_key_values_counter_func";

    private static final String KEY_FIELD_NAME = "keys";
    private Set<MultiKeyFeature> keys;

    public AggrFeatureDistinctValuesCounterFunc() {
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
    protected AggrFeatureValue calculateHistogramAggrFeatureValue(MultiKeyHistogram multiKeyHistogram) {
        long numOfDistinctValues = 0;

        Map<MultiKeyFeature, Double> histogram = multiKeyHistogram.getHistogram();

        //sum all if no keys were defined
        if (keys.isEmpty()) {
            return new AggrFeatureValue(multiKeyHistogram.getN());
        } else {
            //sum all max values of histogram, whose contain one of the keys (e.g: operationType=FILE_OPENED)
            for (Map.Entry<MultiKeyFeature, Double> multiKeyRecordEntry : histogram.entrySet()) {
                for (MultiKeyFeature key : keys) {
                    if (multiKeyRecordEntry.getKey().contains(key)) {
                        numOfDistinctValues++;
                        break;
                    }
                }
            }
            return new AggrFeatureValue(numOfDistinctValues);
        }
    }
}
