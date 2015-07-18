package fortscale.streaming.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;

/**
 * Created by amira on 17/06/2015.
 */
@JsonTypeName(AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureHistogramFunc implements AggrFeatureFunction{
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_histogram_func";

    /**
     * Updates the histogram within aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     * @param aggregatedFeatureConf
     * @param features
     * @param aggrFeature the aggregated feature to update. The aggrFeature's value must be of type {@link GenericHistogram}
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null  or aggrFeature is null or
     * if it's value is not of type {@link GenericHistogram}
     */
    @Override
    public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {

        if(aggregatedFeatureConf==null || aggrFeature==null) {
            return null;
        }

        Object value = aggrFeature.getValue();
        if(value==null) {
            value = new GenericHistogram();
            aggrFeature.setValue(value);
        } else if (value instanceof GenericHistogram == false) {
            return null;
        }
        GenericHistogram histogram = (GenericHistogram)value;

        if(features!=null) {
            List<String> featureNames = aggregatedFeatureConf.getFeatureNames();

            for (int i = 0; i < featureNames.size(); i++) {
                String featureName = featureNames.get(i);
                Feature feature = features.get(featureName);
                if (feature != null) {
                	histogram.add(feature.getValue(), 1.0);
                }
            }
        }

        return histogram;
    }
}
