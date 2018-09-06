package fortscale.utils;

import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AggrFeatureFunctionUtils {

    /**
     * Extract groupBy feature names and values of features map and build MultiKeyFeature.
     * @param features map of feature name to feature value
     * @param groupByFeatureNames groupBy feature names
     * @return MultiKeyFeature
     */
    public static MultiKeyFeature extractGroupByFeatureValues(Map<String, Feature> features, List<String> groupByFeatureNames) {
        if (groupByFeatureNames == null) return new MultiKeyFeature(new HashMap<>());
        Map<String, FeatureValue> featureNameToFeatureValue = new HashMap<>();

        for (String groupByFeatureName : groupByFeatureNames) {
            Feature groupByFeature = features.get(groupByFeatureName);
            if (groupByFeature == null) return null;

            FeatureValue groupByFeatureValue = groupByFeature.getValue();
            if (groupByFeatureValue == null || (groupByFeatureValue instanceof FeatureStringValue && StringUtils.isBlank((String) ((FeatureStringValue) groupByFeatureValue).getValue()))) {
                groupByFeatureValue = new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE);
            }

            featureNameToFeatureValue.put(groupByFeatureName, groupByFeatureValue);
        }
        return new MultiKeyFeature(featureNameToFeatureValue);
    }

    /**
     * Build MultiKeyFeature of features map
     * @param features map of feature name to feature value
     * @return MultiKeyFeature
     */
    public static MultiKeyFeature buildMultiKeyFeature(Map<String, String> features) {
        Map<String, FeatureValue> featureNameToFeatureValue = new HashMap<>();
        features.forEach((key, value) -> {
            featureNameToFeatureValue.put(key, new FeatureStringValue(value));
        });
        return new MultiKeyFeature(featureNameToFeatureValue);
    }

}
