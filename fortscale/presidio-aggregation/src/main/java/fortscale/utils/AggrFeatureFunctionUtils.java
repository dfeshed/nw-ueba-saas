package fortscale.utils;

import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.common.feature.*;
import fortscale.utils.data.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class AggrFeatureFunctionUtils {

    /**
     * Extract groupBy feature names and values of features map and build MultiKeyFeature.
     *
     * @param features            map of feature name to feature value
     * @param groupByFeatureNames groupBy feature names
     * @return MultiKeyFeature
     */
    public static List<MultiKeyFeature> extractGroupByFeatureValues(Map<String, Feature> features, List<String> groupByFeatureNames) {
        List<MultiKeyFeature> multiKeyFeatures = new ArrayList<>();
        if (groupByFeatureNames == null) {
            multiKeyFeatures.add(new MultiKeyFeature());
            return multiKeyFeatures;
        }

        List<Pair<String, List<String>>> featureNameToValues = new ArrayList<>();
        for (String groupByFeatureName : groupByFeatureNames) {
            Feature groupByFeature = features.get(groupByFeatureName);
            if (groupByFeature != null) {
                FeatureValue groupByFeatureValue = groupByFeature.getValue();
                if (groupByFeatureValue == null || (groupByFeatureValue instanceof FeatureStringValue && StringUtils.isBlank(((FeatureStringValue) groupByFeatureValue).getValue()))) {
                    groupByFeatureValue = new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE);
                }

                if (groupByFeatureValue instanceof FeatureListValue) {
                    featureNameToValues.add(new Pair<>(groupByFeatureName, ((FeatureListValue) groupByFeatureValue).getValue()));
                } else {
                    List<String> values = new ArrayList<>();
                    values.add(groupByFeatureValue.toString());
                    featureNameToValues.add(new Pair<>(groupByFeatureName, values));
                }
            }
        }

        //Go over each value of the first feature and create all possible MultiKeyFeatures
        for (int featureValueIndex = 0; featureValueIndex < featureNameToValues.get(0).getValue().size(); featureValueIndex++) {
            createMultiFeatures(featureNameToValues, new MultiKeyFeature(), multiKeyFeatures, 0, featureValueIndex);
        }

        return multiKeyFeatures;
    }


    /**
     * create multiKeyFeature by traversing every unique path of feature values.
     * if featureNameIndex points on the last feature, then add multiKeyFeature to multiKeyFeatures list.
     *
     * @param featureNameToValues list of pairs<featureName, featureValues >
     * @param multiKeyFeature     multiKeyFeature
     * @param multiKeyFeatures    list of multiKeyFeature
     * @param featureNameIndex    featureNameIndex
     * @param featureValueIndex   featureValueIndex
     */
    private static void createMultiFeatures(List<Pair<String, List<String>>> featureNameToValues, MultiKeyFeature multiKeyFeature, List<MultiKeyFeature> multiKeyFeatures, int featureNameIndex, int featureValueIndex) {
        if (featureValueIndex < featureNameToValues.get(featureNameIndex).getValue().size()) {
            String featureName = featureNameToValues.get(featureNameIndex).getKey();
            String featureValue = featureNameToValues.get(featureNameIndex).getValue().get(featureValueIndex);
            multiKeyFeature.add(featureName, featureValue);
        }

        if (featureNameIndex + 1 == featureNameToValues.size()) {
            multiKeyFeatures.add(multiKeyFeature);
        } else {
            for (int nextFeatureValueIndex = 0; nextFeatureValueIndex < featureNameToValues.get(featureNameIndex + 1).getValue().size(); nextFeatureValueIndex++) {
                createMultiFeatures(featureNameToValues, new MultiKeyFeature(multiKeyFeature), multiKeyFeatures, featureNameIndex + 1, nextFeatureValueIndex);
            }
        }
    }

    /**
     * Build MultiKeyFeature of features map
     *
     * @param features map of feature name to feature value
     * @return MultiKeyFeature
     */
    public static MultiKeyFeature buildMultiKeyFeature(Map<String, String> features) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        features.forEach(multiKeyFeature::add);
        return multiKeyFeature;
    }

}
