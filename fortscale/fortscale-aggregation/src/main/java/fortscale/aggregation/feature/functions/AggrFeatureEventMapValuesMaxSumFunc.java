package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
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
 * 		Suppose a user accesses several machines many times, and each machine access gets some score.
 * 		This class can be used in order to know the sum of the maximal score each machine got.
 *
 * Parameters this class gets from the ASL:
 * 1. pick: refer to {@link AbstractAggrFeatureEventFeatureToMaxMapFunc}'s documentation to learn more.
 * 2. includeValues: a boolean which indicates whether additional information should be recorded into the aggregated
 *    feature's value. The recorded information is the features groups found in the aggregated buckets' mappings.
 * 3. minScoreToInclude: feature groups whose maximal score is below the given number won't be recorded.
 */
@JsonTypeName(AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventMapValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxMapFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_map_values_max_sum_func";
	private final static String FEATURE_DISTINCT_VALUES = "distinct_values";

	private boolean includeValues;
	private int minScoreToInclude;

	@Override
	protected AggrFeatureValue calculateFeaturesGroupToMaxValue(AggrFeatureValue aggrFeatureValue) {
		Map<String, Integer> featuresGroupToMax = (Map<String, Integer>) aggrFeatureValue.getValue();
		int sum = 0;
		for (int max : featuresGroupToMax.values()) {
			sum += max;
		}
		AggrFeatureValue res = new AggrFeatureValue(sum, aggrFeatureValue.getTotal());
		putAdditionalInformation(res, featuresGroupToMax);
		return res;
	}

	protected void putAdditionalInformation(AggrFeatureValue aggrFeatureValue, Map<String, Integer> featuresGroupToMax){
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
}
