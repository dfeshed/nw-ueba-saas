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
                if (groupByFeatureValue == null || (groupByFeatureValue instanceof FeatureStringValue && StringUtils.isBlank(((FeatureStringValue) groupByFeatureValue).getValue()))
                        || (groupByFeatureValue instanceof FeatureListValue && ((FeatureListValue) groupByFeatureValue).getValue().isEmpty())) {
                    groupByFeatureValue = new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE);
                }

                if (groupByFeatureValue instanceof FeatureListValue) {
                    featureNameToValues.add(new Pair<>(groupByFeatureName, ((FeatureListValue) groupByFeatureValue).getValue()));
                } else {
                    List<String> values = Collections.singletonList(groupByFeatureValue.toString());
                    featureNameToValues.add(new Pair<>(groupByFeatureName, values));
                }
            }
        }

        createMultiFeatures(featureNameToValues, new MultiKeyFeature(), multiKeyFeatures, 0);
        return multiKeyFeatures;
    }


    /**
     * create multiKeyFeature by traversing every unique path of feature values.
     *
     * @param featureNameToValuesPairs list of pairs<featureName, featureValues >
     * @param multiKeyFeature          multiKeyFeature
     * @param multiKeyFeatures         list of multiKeyFeature
     * @param featureNameIndex         featureNameIndex
     */
    private static void createMultiFeatures(List<Pair<String, List<String>>> featureNameToValuesPairs, MultiKeyFeature multiKeyFeature, List<MultiKeyFeature> multiKeyFeatures, int featureNameIndex) {
        Pair<String, List<String>> featureNameToValuesPair = featureNameToValuesPairs.get(featureNameIndex);
        String featureName = featureNameToValuesPair.getKey();
        List<String> featureNameToValues = featureNameToValuesPair.getValue();

        for (int featureValueIndex = 0; featureValueIndex < featureNameToValues.size(); featureValueIndex++) {
            MultiKeyFeature multiKeyFeatureToAdd = new MultiKeyFeature(multiKeyFeature);
            String featureValue = featureNameToValues.get(featureValueIndex);
            multiKeyFeatureToAdd.add(featureName, featureValue);

            if (featureNameIndex == featureNameToValuesPairs.size() - 1) {
                multiKeyFeatures.add(multiKeyFeatureToAdd);
            } else {
                createMultiFeatures(featureNameToValuesPairs, multiKeyFeatureToAdd, multiKeyFeatures, featureNameIndex + 1);
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
