package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.Feature;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some util functions which are valuable when testing AggrFeature*Func & AggrFeatureEvent*Func classes
 * which are related to maintaining a map from a feature to a max number (e.g. - AggrFeatureFeatureToMaxMapFunc
 * generates such a mapping, and AggrFeatureEventMapValuesMaxSumFunc consumes them).
 */
public class AggrFeatureFeatureToMaxRelatedFuncTestUtils {
    public static AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, String pickFeatureName) {
        List<String> pickFeatureNameList = new ArrayList<>();
        pickFeatureNameList.add(pickFeatureName);
        Map<String, List<String>> map = new HashMap<>();
        map.put(AbstractAggrFeatureEventFeatureToMaxFunc.PICK_FIELD_NAME, pickFeatureNameList);
        return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, map, Mockito.mock(IAggrFeatureEventFunction.class));
    }

    @SafeVarargs
    public static Feature createAggrFeature(String featureName, Pair<MultiKeyFeature, Integer>... featureValuesAndNumbers) {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        for (Pair<MultiKeyFeature, Integer> featureValuesAndNumber : featureValuesAndNumbers) {
            MultiKeyFeature multiKeyFeature = featureValuesAndNumber.getLeft();
            multiKeyHistogram.set(multiKeyFeature, featureValuesAndNumber.getRight().doubleValue());
        }

        return new Feature(featureName, multiKeyHistogram);
    }

    @SafeVarargs
    public static Map<String, Feature> createBucketAggrFeaturesMap(String featureName, Pair<MultiKeyFeature, Integer>... featureValuesAndNumbers) {
        Map<String, Feature> bucketAggrFeaturesMap = new HashMap<>();
        bucketAggrFeaturesMap.put(featureName, createAggrFeature(featureName, featureValuesAndNumbers));
        return bucketAggrFeaturesMap;
    }

    @SafeVarargs
    public static List<Map<String, Feature>> createMultipleBucketsAggrFeaturesMapList(String featureName, Pair<MultiKeyFeature, Integer>[]... featureValuesAndNumbersInBucketList) {
        List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = new ArrayList<>();

        for (Pair<MultiKeyFeature, Integer>[] featureValuesAndNumbers : featureValuesAndNumbersInBucketList) {
            Map<String, Feature> bucketAggrFeaturesMap = createBucketAggrFeaturesMap(featureName, featureValuesAndNumbers);
            multipleBucketsAggrFeaturesMapList.add(bucketAggrFeaturesMap);
        }

        return multipleBucketsAggrFeaturesMapList;
    }
}
