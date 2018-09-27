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
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventHistogramMaxCountObjectFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHistogramMaxCountObjectFunc extends AbstractAggrFeatureEventHistogram {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_max_count_obj_func";

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(MultiKeyHistogram multiKeyHistogram) {
		return new AggrFeatureValue(multiKeyHistogram.getMaxObject(), (long)multiKeyHistogram.getTotal());
	}


}
