package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some util functions which are valuable when testing AggrFeature*Func classes.
 */
public class AggrFeatureTestUtils {
    @SafeVarargs
    public static Map<String, Feature> createFeatureMap(final ImmutablePair<String, Object>... featureValues) {
        Map<String, Feature> featureMap = new HashMap<>();

        for (ImmutablePair<String, Object> featureValue : featureValues) {
            Object value = featureValue.getRight();

            if (value instanceof String) {
                featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (String)value));
            } else if (value instanceof Number) {
                featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (Number)value));
            } else if (value == null || (value instanceof FeatureValue)) {
                featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (FeatureValue)value));
            } else {
                throw new IllegalArgumentException();
            }
        }

        return featureMap;
    }

    public static AggregatedFeatureConf createAggrFeatureConf(int num) {
        List<String> featureNames = new ArrayList<>();
        for (int i = 1; i <= num; i++)
            featureNames.add(String.format("feature%d", i));
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, featureNames);
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, Mockito.mock(IAggrFeatureFunction.class));
    }

    public static AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= num; i++)
            list.add(String.format("feature%d", i));
        Map<String, List<String>> map = new HashMap<>();
        map.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, list);
        return new AggregatedFeatureEventConf(name, "bucketConfName", "aggregated_feature_event_type_F", 3, 1, map, Mockito.mock(IAggrFeatureEventFunction.class));
    }

    public static MultiKeyFeature createMultiKeyFeatureWithOneFeature(String featureName, String featureValue) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        multiKeyFeature.add(featureName, featureValue);
        return multiKeyFeature;
    }

    public static MultiKeyFeature createMultiKeyFeature(Map<String, String> featureNameToValues) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        featureNameToValues.forEach(multiKeyFeature::add);
        return multiKeyFeature;
    }
}
