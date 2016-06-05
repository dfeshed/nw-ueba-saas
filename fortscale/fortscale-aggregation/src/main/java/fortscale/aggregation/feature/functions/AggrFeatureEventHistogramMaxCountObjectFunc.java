package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.datastructures.GenericHistogram;
import fortscale.common.feature.AggrFeatureValue;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventHistogramMaxCountObjectFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHistogramMaxCountObjectFunc extends AbstractAggrFeatureEventHistogram {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_max_count_obj_func";

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
		return new AggrFeatureValue(histogram.getMaxCountObject(), (long)histogram.getTotalCount());
	}


}
