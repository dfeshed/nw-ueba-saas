package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;

import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */
public class AggrFeatureHistogramFunc implements AggrFeatureFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_func";

    @Override
    public Object updateAggrFeature(Map<String, Feature> features, Feature aggrFeature) {
        //ToDo: implement
        return null;
    }
}
