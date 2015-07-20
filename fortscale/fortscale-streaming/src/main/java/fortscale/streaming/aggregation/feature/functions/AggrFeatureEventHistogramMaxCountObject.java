package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 20/07/2015.
 */
@JsonTypeName(AggrFeatureEventHistogramMaxCountObject.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHistogramMaxCountObject extends AggrFeatureHistogramFunc {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_max_count_obj_func";
    public final static String FEATURE_NAME = "max_cout_object";
    /**
     * Create new feature by running the associated {@link AggrFeatureFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        Feature feature = super.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        if(feature==null || feature.getValue()==null) {
            return null;
        }
        GenericHistogram histogram = (GenericHistogram)feature.getValue();
        Feature resFeature = new Feature(FEATURE_NAME, histogram.getMaxCountObject());

        return resFeature;
    }
}
