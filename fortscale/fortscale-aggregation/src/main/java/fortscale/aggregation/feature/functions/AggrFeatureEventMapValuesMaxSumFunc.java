package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Map;

@JsonTypeName(AggrFeatureEventMapValuesMaxSumFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventMapValuesMaxSumFunc extends AbstractAggrFeatureEventFeatureToMaxMapFunc {
    public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_map_values_max_sum_func";

	@Override
	protected AggrFeatureValue calculateMapAggrFeatureValue(AggrFeatureValue aggrFeatureValue) {
		Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) aggrFeatureValue.getValue();
		int sum = 0;
		for (int max : featuresGroupToMax.values()) {
			sum += max;
		}
		return new AggrFeatureValue(sum, (long) featuresGroupToMax.size());
	}
}
