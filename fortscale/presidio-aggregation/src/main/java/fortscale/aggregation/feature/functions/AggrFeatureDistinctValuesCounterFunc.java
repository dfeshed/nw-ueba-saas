package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.util.GenericHistogram;

/**
 * @author Amir Ahinoam
 * @author Lior Govrin
 */
@JsonTypeName(AggrFeatureDistinctValuesCounterFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureDistinctValuesCounterFunc extends AbstractAggrFeatureEventHistogram {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_distinct_values_counter_func";

    @Override
    protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
        return new AggrFeatureValue(histogram.getN(), (long)histogram.getTotalCount());
    }
}
