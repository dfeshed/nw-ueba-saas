package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate one or more buckets containing a feature containing a mapping from features group to max value.
 * Such a mapping (of type Map<List<String>, Integer>) is created by AggrFeatureFeatureToMaxMapFunc.
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
 * 2. includeValues: a boolean which indicates whether additional information should be recorded into the aggregated
 *    feature's value. The recorded information is the features groups found in the aggregated buckets' mappings.
 * 3. minScoreToInclude: feature groups whose maximal score is below the given number won't be recorded.
 */
@JsonTypeName(AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class AggrFeatureEventMapValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxMapFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_map_values_max_sum_func";
    private final static String FEATURE_DISTINCT_VALUES = "distinct_values";

    @JsonProperty("includeValues")
    private boolean includeValues;
    @JsonProperty("minScoreToInclude")
    private int minScoreToInclude;
    @JsonProperty("pattern")
    private String pattern;
    @JsonProperty("replacement")
    private String replacement;
    @JsonProperty("postCondition")
    private String postCondition;

    @Override
    protected AggrFeatureValue calculateFeaturesGroupToMaxValue(AggrFeatureValue aggrFeatureValue) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> featuresGroupToMax = (Map<String, Integer>)aggrFeatureValue.getValue();
        Map<String, Integer> clusterToMaxValueMap = getClusterToMaxValueMap(featuresGroupToMax);

        int sum = 0;
        for (int max : clusterToMaxValueMap.values()) {
            sum += max;
        }

        AggrFeatureValue res = new AggrFeatureValue(sum, aggrFeatureValue.getTotal());
        putAdditionalInformation(res, featuresGroupToMax); // TODO: Add this map or the new clusters map?
        return res;
    }

    private void putAdditionalInformation(AggrFeatureValue aggrFeatureValue, Map<String, Integer> featuresGroupToMax) {
        if (!includeValues) {
            return;
        }

        List<String> featuresGroupsWithHighScore = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : featuresGroupToMax.entrySet()) {
            if (entry.getValue() >= minScoreToInclude) {
                featuresGroupsWithHighScore.add(entry.getKey());
            }
        }

        aggrFeatureValue.putAdditionalInformation(FEATURE_DISTINCT_VALUES, featuresGroupsWithHighScore);
    }

    private Map<String, Integer> getClusterToMaxValueMap(Map<String, Integer> featureToMaxValueMap) {
        if (StringUtils.isEmpty(pattern) || replacement == null) return featureToMaxValueMap;
        Map<String, Integer> clusterToMaxValueMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : featureToMaxValueMap.entrySet()) {
            String before = entry.getKey();
            Integer maxValue = entry.getValue();
            if (before == null || maxValue == null) continue;

            String after = before.replaceAll(pattern, replacement);
            String cluster = postCondition != null && !after.matches(postCondition) ? before : after;

            if (clusterToMaxValueMap.containsKey(cluster)) {
                clusterToMaxValueMap.put(cluster, Math.max(clusterToMaxValueMap.get(cluster), maxValue));
            } else {
                clusterToMaxValueMap.put(cluster, maxValue);
            }
        }

        return clusterToMaxValueMap;
    }
}
