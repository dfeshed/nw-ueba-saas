package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventHistogramMaxCountObjectFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHistogramMaxCountObjectFunc extends AbstractAggrFeatureEventHistogram {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_max_count_obj_func";
    /**
     * Create new feature by running the associated {@link IAggrFeatureFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */

	@Override
	protected AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram) {
		return new AggrFeatureValue(histogram.getMaxCountObject());
	}


}
