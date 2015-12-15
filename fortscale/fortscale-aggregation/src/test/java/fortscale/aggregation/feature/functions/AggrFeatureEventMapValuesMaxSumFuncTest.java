package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AggrFeatureEventMapValuesMaxSumFuncTest {

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, String pluckFeatureName) {
		List<String> pluckFeatureNameList = new ArrayList<>();
		pluckFeatureNameList.add(pluckFeatureName);
		Map<String, List<String>> map = new HashMap<>();
		map.put(AbstractAggrFeatureEventFeatureToMaxMapFunc.PLUCK_FIELD_NAME, pluckFeatureNameList);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "HIGHEST_SCORE", map, new JSONObject());
	}

	private Map<String, Feature> createBucketAggrFeaturesMap(String featureName, Pair<String[], Integer>... featureValuesAndNumbers) {
		Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
		for (Pair<String[], Integer> featureValuesAndNumber : featureValuesAndNumbers) {
			List<String> featureGroupedByValues = Arrays.asList(featureValuesAndNumber.getLeft());
			featuresGroupToMax.put(featureGroupedByValues, featureValuesAndNumber.getRight());
		}
		Feature aggrFeature = new Feature(featureName, new AggrFeatureValue(featuresGroupToMax, (long) featuresGroupToMax.size()));
		Map<String, Feature> bucketAggrFeaturesMap = new HashMap<>();
		bucketAggrFeaturesMap.put(featureName, aggrFeature);
		return bucketAggrFeaturesMap;
	}

	@Test
	public void testCalculateAggrFeature() {
		int max1 = 10;
		int max2 = 20;
		String pluckFeatureName = "source_machine_to_highest_score";
		List<Map<String, Feature>> listOfMaps = new ArrayList<>();
		listOfMaps.add(createBucketAggrFeaturesMap(
				pluckFeatureName,
				new ImmutablePair<>(new String[]{"host_123"}, max1),
				new ImmutablePair<>(new String[]{"host_456"}, max2)));

		String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
		Feature res = new AggrFeatureEventMapValuesMaxSumFunc().calculateAggrFeature(
				createAggregatedFeatureEventConf(aggregatedFeatureName, pluckFeatureName),
				listOfMaps);

		Assert.assertEquals(max1 + max2, ((AggrFeatureValue) res.getValue()).getValue());
	}
}
