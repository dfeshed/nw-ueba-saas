package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 17/06/2015.
 */
public class AggrFeatureAvgStdNFunc implements AggrFeatureFunction, AggrFeatureEventFunction {
    final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_av_std_n_func";


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
            for (int i = 0; i < featureNames.size(); i++) {
                String featureName = featureNames.get(i);
                Feature feature = features.get(featureName);

                if(feature!=null) {
                    try {
                        Double doubleValue = (Double) feature.getValue();
                        avgStdN.add(doubleValue);
                    } catch (ClassCastException e) {
                        // Value ignored
                    }
                }
            }
        }
        return avgStdN;

     }

    /**
     * Create new feature by running the associated {@link AggrFeatureEventFunction} that is configured in the given
     * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
     *
     * @param aggrFeatureEventConf               the specification of the feature to be created
     * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
     * @return a new feature created by the relevant function.
     */
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        if(aggrFeatureEventConf==null || multipleBucketsAggrFeaturesMapList == null) {
            return null;
        }
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        Feature resFeature = new Feature(aggrFeatureEventConf.getName(), avgStdN);

        for(Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
            for(String featureName: aggrFeatureEventConf.getAggregatedFeatureNamesList()) {
                Feature aggrFeature = aggrFeatures.get(featureName);
                if(aggrFeature!=null && aggrFeature.getValue() instanceof ContinuousValueAvgStdN) {
                    avgStdN.add((ContinuousValueAvgStdN)aggrFeature.getValue());
                } else {
                    return null;
                    //TODO throw exception instead?
                }
            }
        }

        return resFeature;
    }
}
