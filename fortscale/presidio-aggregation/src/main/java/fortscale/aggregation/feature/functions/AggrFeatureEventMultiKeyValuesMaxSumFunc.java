package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregate one or more buckets containing a feature that is a mapping from a group (a context) to a max value.
 * Such a mapping (of type {@link MultiKeyHistogram}) is created by {@link AggrFeatureMultiKeyToMaxFunc}.
 * First, {@link AbstractAggrFeatureEventFeatureToMaxFunc} is used in order to aggregate multiple feature buckets
 * (refer to its documentation to learn more). Then, all the max values (or max values that are filtered in according
 * to their groups) are summed up in order to create a new aggregated feature event.
 *
 * For example: Suppose a user accesses several machines many times, and each machine access gets a score.
 * This function can be used in order to calculate the sum of all the maximal scores (maximal score per machine).
 *
 * Parameters this function gets from the ASL:
 * 1. pick: Refer to {@link AbstractAggrFeatureEventFeatureToMaxFunc}'s documentation to learn more.
 */
@JsonTypeName(AggrFeatureEventMultiKeyValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        creatorVisibility = Visibility.ANY,
        fieldVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public class AggrFeatureEventMultiKeyValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxFunc {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_multi_key_values_max_sum_func";

    private final List<MultiKeyFeature> contextsToFilterIn;

    @JsonCreator
    public AggrFeatureEventMultiKeyValuesMaxSumFunc(
            @JsonProperty("contextsToFilterIn") List<Map<String, String>> contextsToFilterIn) {

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
        contextToMaxValueMap = filterOutIrrelevantContexts(contextToMaxValueMap);
        double maxValuesSum = sumMaxValues(contextToMaxValueMap);
        return new AggrFeatureValue(maxValuesSum);
    }

    @Override
    public MultiKeyHistogram calculateContributionRatios(
            AggregatedFeatureEventConf aggregatedFeatureEventConf, FeatureBucket featureBucket) {

        // Extract the aggregated feature from the feature bucket.
        String aggregatedFeatureName = getNameOfAggregatedFeatureToPick(aggregatedFeatureEventConf);
        MultiKeyHistogram contextToMaxValueMap = (MultiKeyHistogram)featureBucket
                .getAggregatedFeatures().get(aggregatedFeatureName).getValue();
        contextToMaxValueMap = filterOutIrrelevantContexts(contextToMaxValueMap);
        double maxValuesSum = sumMaxValues(contextToMaxValueMap);
        MultiKeyHistogram contextToContributionRatioMap = new MultiKeyHistogram();
        contextToMaxValueMap.getHistogram().forEach((context, maxValue) -> {
            double contributionRatio = maxValuesSum == 0 ? 0 : maxValue / maxValuesSum;
            contextToContributionRatioMap.set(context, contributionRatio);
        });
        return contextToContributionRatioMap;
    }

    private MultiKeyHistogram filterOutIrrelevantContexts(MultiKeyHistogram oldContextToMaxValueMap) {
        if (contextsToFilterIn.isEmpty()) return oldContextToMaxValueMap;
        MultiKeyHistogram newContextToMaxValueMap = new MultiKeyHistogram();
        oldContextToMaxValueMap.getHistogram().forEach((context, maxValue) -> {
            for (MultiKeyFeature contextToFilterIn : contextsToFilterIn) {
                if (context.contains(contextToFilterIn)) {
                    newContextToMaxValueMap.set(context, maxValue);
                    break;
                }
            }
        });
        return newContextToMaxValueMap;
    }

    private double sumMaxValues(MultiKeyHistogram contextToMaxValueMap) {
        return contextToMaxValueMap.getHistogram().values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
