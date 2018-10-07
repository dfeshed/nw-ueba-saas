package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventHistogramMaxCountObjectFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHistogramMaxCountObjectFunc extends AbstractAggrFeatureEventHistogram {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_max_count_obj_func";

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(MultiKeyHistogram multiKeyHistogram) {
		MultiKeyFeature max = multiKeyHistogram.getHistogram().entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
		return new AggrFeatureValue(max, (long)multiKeyHistogram.getTotal());
	}


}
