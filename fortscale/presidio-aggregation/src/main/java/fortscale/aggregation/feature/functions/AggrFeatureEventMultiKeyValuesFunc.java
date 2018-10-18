package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.AggrFeatureFunctionUtils;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This aggregation event function combines histograms from multiple feature buckets, each histogram maps a key to its
 * number of appearances (key -> counter). If the combined histogram is empty, the function returns null, otherwise it
 * returns the number of appearances of a certain key (defined as a static parameter in the function's ASL).
 * <p>
 * For example - An aggregation event that is "number of successful deletions":
 * [1] In the feature buckets, filter in enriched records of type
 * "deletion" and define a histogram for the values of "result".
 * [2] If the combined histogram of a certain time range is empty, the aggregation event of that time
 * range will not be generated (there weren't any type of deletions, nor successful nor failed).
 * [3] Otherwise, the function will output the value of the histogram key "success" (0 if the key doesn't exist).
 *
 * @author Lior Govrin
 */
@JsonTypeName(AggrFeatureEventMultiKeyValuesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureEventMultiKeyValuesFunc extends AbstractAggrFeatureEventHistogram {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_multi_key_values_func";
    private static final String KEY_FIELD_NAME = "keys";
    private Set<MultiKeyFeature> keys;


    @JsonProperty(KEY_FIELD_NAME)
    public void setKeys(Set<Map<String, String>> keys) {
        keys.forEach(features -> {
            MultiKeyFeature featureNamesAndValues = AggrFeatureFunctionUtils.buildMultiKeyFeature(features);
            this.keys.add(featureNamesAndValues);
        });
    }

    public AggrFeatureEventMultiKeyValuesFunc() {
        this.keys = new HashSet<>();
    }

    @Override
    protected AggrFeatureValue calculateHistogramAggrFeatureValue(MultiKeyHistogram multiKeyHistogram) {
        AggrFeatureValue aggrFeatureValue = null;
        Double sum = 0D;

        if (multiKeyHistogram != null) {
            Map<MultiKeyFeature, Double> histogram = multiKeyHistogram.getHistogram();

            //sum all if no keys defined
            if (keys.isEmpty()) {
                sum = histogram.values().stream().mapToDouble(v -> v).sum();
            } else {
                //sum all values of histogram, whose contain one of the keys (e.g: operationType=FILE_OPENED)
                for (Map.Entry<MultiKeyFeature, Double> multiKeyRecordEntry : histogram.entrySet()) {
                    for (MultiKeyFeature key : keys) {
                        if (multiKeyRecordEntry.getKey().contains(key)) {
                            Double counter = multiKeyRecordEntry.getValue();
                            counter = counter == null ? 0 : counter;
                            if (!counter.isNaN() && !counter.isInfinite()) {
                                sum += counter;
                            }
                            break;
                        }
                    }
                }
            }

            aggrFeatureValue = new AggrFeatureValue(sum);
        }
        return aggrFeatureValue;
    }
}
