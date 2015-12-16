package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonTypeName(AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventMapValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxMapFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_map_values_max_sum_func";
	private final static String FEATURE_DISTINCT_VALUES = "distinct_values";

	private boolean includeValues;
	private int minScoreToInclude;

	@Override
	protected AggrFeatureValue calculateMapAggrFeatureValue(AggrFeatureValue aggrFeatureValue) {
		Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) aggrFeatureValue.getValue();
		int sum = 0;
		for (int max : featuresGroupToMax.values()) {
			sum += max;
		}
		AggrFeatureValue res = new AggrFeatureValue(sum, (long) featuresGroupToMax.size());
		putAdditionalInformation(res, featuresGroupToMax);
		return res;
	}

	protected void putAdditionalInformation(AggrFeatureValue aggrFeatureValue, Map<List<String>, Integer> featuresGroupToMax){
		if (!includeValues) {
			return;
		}

		List<List<String>> featuresGroupsWithHighScore = new ArrayList<>();
		for (Map.Entry<List<String>, Integer> entry : featuresGroupToMax.entrySet()) {
			if (entry.getValue() >= minScoreToInclude) {
				featuresGroupsWithHighScore.add(entry.getKey());
			}
		}
		aggrFeatureValue.putAdditionalInformation(FEATURE_DISTINCT_VALUES, featuresGroupsWithHighScore);
	}
}
