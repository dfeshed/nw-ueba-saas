package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

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
        map.put(AbstractAggrFeatureEventFeatureToMaxMapFunc.PICK_FIELD_NAME, pickFeatureNameList);
        return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, map, new JSONObject());
    }


    public static Feature createAggrFeature(String featureName, Pair<String[], Integer>... featureValuesAndNumbers) {
        Map<String, Integer> featuresGroupToMax = new HashMap<>();
        for (Pair<String[], Integer> featureValuesAndNumber : featureValuesAndNumbers) {
            List<String> featureGroupedByValues = Arrays.asList(featureValuesAndNumber.getLeft());
            featuresGroupToMax.put(StringUtils.join(featureGroupedByValues,"# # #"), featureValuesAndNumber.getRight());
        }
        return new Feature(featureName, new AggrFeatureValue(featuresGroupToMax, (long) featuresGroupToMax.size()));
    }

    public static Map<String, Feature> createBucketAggrFeaturesMap(String featureName, Pair<String[], Integer>... featureValuesAndNumbers) {
        Map<String, Feature> bucketAggrFeaturesMap = new HashMap<>();
        bucketAggrFeaturesMap.put(featureName, createAggrFeature(featureName, featureValuesAndNumbers));
        return bucketAggrFeaturesMap;
    }

    public static List<Map<String, Feature>> createMultipleBucketsAggrFeaturesMapList(String featureName, Pair<String[], Integer>[]... featureValuesAndNumbersInBucketList) {
        List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = new ArrayList<>();
        for (Pair<String[], Integer>[] featureValuesAndNumbers : featureValuesAndNumbersInBucketList) {
            Map<String, Feature> bucketAggrFeaturesMap = createBucketAggrFeaturesMap(featureName, featureValuesAndNumbers);
            multipleBucketsAggrFeaturesMapList.add(bucketAggrFeaturesMap);
        }
        return multipleBucketsAggrFeaturesMapList;
    }
}

