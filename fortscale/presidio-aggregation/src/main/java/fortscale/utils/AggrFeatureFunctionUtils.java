package fortscale.utils;

import fortscale.aggregation.feature.functions.AggGenericNAFeatureValues;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.data.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        if (groupByFeatureNames == null) return multiKeyFeature;

        for (String groupByFeatureName : groupByFeatureNames) {
            Feature groupByFeature = features.get(groupByFeatureName);
            if (groupByFeature == null) return null;

            FeatureValue groupByFeatureValue = groupByFeature.getValue();
            if (groupByFeatureValue == null || (groupByFeatureValue instanceof FeatureStringValue && StringUtils.isBlank((String) ((FeatureStringValue) groupByFeatureValue).getValue()))) {
                groupByFeatureValue = new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE);
            }
            multiKeyFeature.add(groupByFeatureName, groupByFeatureValue);
        }
        return multiKeyFeature;
    }

    /**
     * Build MultiKeyFeature of features map
     * @param features map of feature name to feature value
     * @return MultiKeyFeature
     */
    public static MultiKeyFeature buildMultiKeyFeature(Map<String, String> features) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        features.forEach((key, value) -> {
            multiKeyFeature.add(key, new FeatureStringValue(value));
        });
        return multiKeyFeature;
    }

}
