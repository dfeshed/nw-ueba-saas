package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Some util functions which are valuable when testing AggrFeature*Func & AggrFeatureEvent*Func classes
 * which are related to maintaining a map from a feature to a max number (e.g. - AggrFeatureFeatureToMaxMapFunc
 * generates such a mapping, and AggrFeatureEventMapValuesMaxSumFunc consumes them).
 */
public class AggrFeatureFeatureToMaxRelatedFuncTestUtils {

	public static AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, String pluckFeatureName) {
		List<String> pluckFeatureNameList = new ArrayList<>();
		pluckFeatureNameList.add(pluckFeatureName);
		Map<String, List<String>> map = new HashMap<>();
		map.put(AbstractAggrFeatureEventFeatureToMaxMapFunc.PLUCK_FIELD_NAME, pluckFeatureNameList);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "HIGHEST_SCORE", map, new JSONObject());
	}

	public static Map<String, Feature> createFeatureMap(final ImmutablePair<String, Object>... featureValues) {
		Map<String, Feature> featureMap = new HashMap<>();
		for (ImmutablePair<String, Object> featureValue : featureValues) {
			Object value = featureValue.getRight();
			if (value instanceof String) {
				featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (String) value));
			} else {
				featureMap.put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (Integer) value));
			}
		}
		return featureMap;
	}

	public static Feature createAggrFeature(String featureName, Pair<String[], Integer>... featureValuesAndNumbers) {
		Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
		for (Pair<String[], Integer> featureValuesAndNumber : featureValuesAndNumbers) {
			List<String> featureGroupedByValues = Arrays.asList(featureValuesAndNumber.getLeft());
			featuresGroupToMax.put(featureGroupedByValues, featureValuesAndNumber.getRight());
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
