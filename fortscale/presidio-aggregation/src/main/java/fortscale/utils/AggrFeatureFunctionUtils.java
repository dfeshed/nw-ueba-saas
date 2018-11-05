package fortscale.utils;

import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.common.feature.*;
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

        createMultiKeyFeatureList(features, groupByFeatureNames, multiKeyFeatures);

        for (String groupByFeatureName : groupByFeatureNames) {
            Feature groupByFeature = features.get(groupByFeatureName);
            if (groupByFeature != null) {
                FeatureValue groupByFeatureValue = groupByFeature.getValue();
                if (!(groupByFeatureValue instanceof FeatureListValue)) {
                    if (groupByFeatureValue == null || (groupByFeatureValue instanceof FeatureStringValue && StringUtils.isBlank(((FeatureStringValue) groupByFeatureValue).getValue()))) {
                        groupByFeatureValue = new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE);
                    }

                    if (multiKeyFeatures.isEmpty()) {
                        multiKeyFeatures.add(new MultiKeyFeature());
                    }
                    for (MultiKeyFeature multiKeyFeature : multiKeyFeatures) {
                        multiKeyFeature.add(groupByFeatureName, groupByFeatureValue.toString());
                    }
                }
            }
        }

        return multiKeyFeatures;
    }


    /**
     * Go over groupByFeatureNames
     * if groupByFeatureName is a List, then create new MultiKeyFeature for each item
     *
     * @param features            features
     * @param groupByFeatureNames groupByFeatureNames
     * @return MultiKeyFeature list
     */
    private static List<MultiKeyFeature> createMultiKeyFeatureList(Map<String, Feature> features, List<String> groupByFeatureNames, List<MultiKeyFeature> multiKeyFeatures) {
        groupByFeatureNames.forEach(groupByFeatureName -> {
            Feature groupByFeature = features.get(groupByFeatureName);
            if (groupByFeature != null) {
                FeatureValue groupByFeatureValue = groupByFeature.getValue();
                if (groupByFeatureValue instanceof FeatureListValue) {
                    (((FeatureListValue) groupByFeatureValue).getValue()).forEach(value -> {
                        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
                        multiKeyFeature.add(groupByFeatureName, value);
                        multiKeyFeatures.add(multiKeyFeature);
                    });
                }
            }
        });

        return multiKeyFeatures;
    }

    /**
     * Build MultiKeyFeature of features map
     * @param features map of feature name to feature value
     * @return MultiKeyFeature
     */
    public static MultiKeyFeature buildMultiKeyFeature(Map<String, String> features) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        features.forEach(multiKeyFeature::add);
        return multiKeyFeature;
    }

}
