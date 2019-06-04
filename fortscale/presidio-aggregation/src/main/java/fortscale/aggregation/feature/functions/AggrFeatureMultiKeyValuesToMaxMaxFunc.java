package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;


@JsonTypeName(AggrFeatureMultiKeyValuesToMaxMaxFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureMultiKeyValuesToMaxMaxFunc extends AbstractAggrFeatureEventFeatureToMaxFunc {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_multi_key_values_to_max_max_func";


    private final List<MultiKeyFeature> contextsToFilterIn;

    @JsonCreator
    public AggrFeatureMultiKeyValuesToMaxMaxFunc(@JsonProperty("contextsToFilterIn") List<Map<String, String>> contextsToFilterIn) {
        this.contextsToFilterIn = contextsToFilterIn == null ? Collections.emptyList() : contextsToFilterIn.stream()
                .map(contextFieldNameToValueMap -> {
                    Assert.notEmpty(contextFieldNameToValueMap, "contextsToFilterIn cannot contain empty maps.");
                    MultiKeyFeature contextToFilterIn = new MultiKeyFeature();
                    contextFieldNameToValueMap.forEach((contextFieldName, contextFieldValue) -> {
                        Assert.hasText(contextFieldName, "contextsToFilterIn cannot contain maps with blank keys.");
                        Assert.hasText(contextFieldValue, "contextsToFilterIn cannot contain maps with blank values.");
                        contextToFilterIn.add(contextFieldName, contextFieldValue);
                    });
                    return contextToFilterIn;
                })
                .collect(Collectors.toList());
    }


    @Override
    protected AggrFeatureValue calculateFeaturesGroupToMaxValue(MultiKeyHistogram contextToMaxValueMap) {
        Map<MultiKeyFeature, Double> histogram = contextToMaxValueMap.getHistogram();

        //sum all if no keys were defined
        OptionalDouble optionalMax;
        if (contextsToFilterIn.isEmpty()) {
            optionalMax =histogram.values().stream().mapToDouble(Double::doubleValue).max();
        } else {
            optionalMax = histogram.entrySet().stream()
                    .filter(entry -> contextsToFilterIn.stream().anyMatch(multiKeyFeature -> entry.getKey().contains(multiKeyFeature)))
                    .mapToDouble(value -> value.getValue())
                    .max();
        }

        double max =  optionalMax.isPresent() ? optionalMax.getAsDouble() : 0.0;

        return new AggrFeatureValue(max);
    }
}
