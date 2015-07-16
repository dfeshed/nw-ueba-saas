package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */
@JsonTypeName(AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureAvgStdNFunc extends AbstractAggrFeatureFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_av_std_n_func";

    public AggrFeatureAvgStdNFunc(@JsonProperty("filter") AggrFilter aggrFilter) {
        super(aggrFilter);
    }

    /**
     * Updates the Average, Standard Deviation and Total feature count (N) within the aggrFeature.
     * Uses the features as input for the function according to the configuration in the aggregatedFeatureConf.
     * @param aggregatedFeatureConf
     * @param features the values of the relevant features must be of type {@link Double}. Values which are not of type
     *                 {@link Double} are ignored.
     * @param aggrFeature the aggregated feature to update. The aggrFeature's value must be of type {@link ContinuousValueAvgStdN}.
     * @return the value of the updated aggrFeature or null if aggregatedFeatureConf is null or aggrFeature is null or
     * if it's value is not of type {@link ContinuousValueAvgStdN}
     */
    @Override
    public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {

       if(aggregatedFeatureConf==null || aggrFeature==null) {
            return null;
        }

        Object value = aggrFeature.getValue();
        if(value==null) {
            value = new ContinuousValueAvgStdN();
            aggrFeature.setValue(value);
        } else if (value instanceof ContinuousValueAvgStdN == false) {
            return null;
        }
        ContinuousValueAvgStdN avgStdN = (ContinuousValueAvgStdN)value;

        List<String> featureNames = aggregatedFeatureConf.getFeatureNames();

        if(features!=null) {
            for (String featureName : featureNames) {
                Feature feature = features.get(featureName);
                if (feature != null) {
                    if (aggrFilter != null) {
                        if (aggrFilter.passedFilter(feature.getName(), feature.getValue())) {
                            addValue(avgStdN, feature.getValue());
                        }
                    }
                    else {
                        addValue(avgStdN, feature.getValue());
                    }
                }
            }
        }
        return avgStdN;
     }

    private void addValue(ContinuousValueAvgStdN avgStdN, Object value) {
        try {
            Double doubleValue = (Double)value;
            avgStdN.add(doubleValue);
        } catch (ClassCastException e) {
            // Value ignored
        }
    }
}
