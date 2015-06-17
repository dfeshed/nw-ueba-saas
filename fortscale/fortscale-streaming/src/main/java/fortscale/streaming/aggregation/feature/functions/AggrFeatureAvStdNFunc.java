package fortscale.streaming.aggregation.feature.functions;

import com.sun.tools.javac.comp.Todo;
import fortscale.streaming.aggregation.feature.Feature;

import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */
public class AggrFeatureAvStdNFunc implements AggrFeatureFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_av_std_n_func";

    @Override
    public Object updateAggrFeature(Map<String, Feature> features, Feature aggrFeature) {
        //Todo: implement
        return null;
    }
}
