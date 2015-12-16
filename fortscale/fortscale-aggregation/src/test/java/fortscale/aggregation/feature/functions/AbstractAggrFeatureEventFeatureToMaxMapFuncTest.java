package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AbstractAggrFeatureEventFeatureToMaxMapFuncTest {

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, String pluckFeatureName) {
		List<String> pluckFeatureNameList = new ArrayList<>();
		pluckFeatureNameList.add(pluckFeatureName);
		Map<String, List<String>> map = new HashMap<>();
		map.put(AbstractAggrFeatureEventFeatureToMaxMapFunc.PLUCK_FIELD_NAME, pluckFeatureNameList);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "HIGHEST_SCORE", map, new JSONObject());
	}

	private Feature createAggrFeature(String featureName, Pair<String[], Integer>... featureValuesAndNumbers) {
		Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
		for (Pair<String[], Integer> featureValuesAndNumber : featureValuesAndNumbers) {
			List<String> featureGroupedByValues = Arrays.asList(featureValuesAndNumber.getLeft());
			featuresGroupToMax.put(featureGroupedByValues, featureValuesAndNumber.getRight());
		}
		return new Feature(featureName, new AggrFeatureValue(featuresGroupToMax, (long) featuresGroupToMax.size()));
	}

	private List<Map<String, Feature>> createMultipleBucketsAggrFeaturesMapList(String featureName, Feature... aggrFeatures) {
		List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = new ArrayList<>();
		for (Feature aggrFeature : aggrFeatures) {
			Map<String, Feature> bucketAggrFeaturesMap = new HashMap<>();
			bucketAggrFeaturesMap.put(featureName, aggrFeature);
			multipleBucketsAggrFeaturesMapList.add(bucketAggrFeaturesMap);
		}
		return multipleBucketsAggrFeaturesMapList;
	}

	@Test
	public void shouldCreateTheSameMappingGivenOnlyOneBucket() {
		String pluckFeatureName = "source_machine_to_highest_score_map";
		final Feature aggrFeature = createAggrFeature(
				pluckFeatureName,
				new ImmutablePair<>(new String[]{"host_123"}, 10));

		final boolean[] calculateMapAggrFeatureValueWasCalled = {false};
		AbstractAggrFeatureEventFeatureToMaxMapFunc f = new AbstractAggrFeatureEventFeatureToMaxMapFunc() {
			@Override
			protected AggrFeatureValue calculateMapAggrFeatureValue(AggrFeatureValue aggrFeatureValue) {
				Assert.assertEquals(aggrFeature.getValue(), aggrFeatureValue);
				calculateMapAggrFeatureValueWasCalled[0] = true;
				return null;
			}
		};

		List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = createMultipleBucketsAggrFeaturesMapList(pluckFeatureName, aggrFeature);
		f.calculateAggrFeature(
				createAggregatedFeatureEventConf("sum_of_highest_scores_over_src_machines_vpn_hourly", pluckFeatureName),
				multipleBucketsAggrFeaturesMapList);

		Assert.assertTrue(calculateMapAggrFeatureValueWasCalled[0]);
	}

	@Test
	public void shouldCreateMappingContainingMaxValuesGivenTwoBuckets() {
		String pluckFeatureName = "source_machine_to_highest_score_map";
		final String feature1 = "host_123";
		final String feature2 = "host_456";
		final int max1 = 10;
		final int max2 = 20;
		final Feature aggrFeature1 = createAggrFeature(
				pluckFeatureName,
				new ImmutablePair<>(new String[]{feature1}, max1),
				new ImmutablePair<>(new String[]{feature2}, max2 - 1));
		final Feature aggrFeature2 = createAggrFeature(
				pluckFeatureName,
				new ImmutablePair<>(new String[]{feature2}, max2));

		final boolean[] calculateMapAggrFeatureValueWasCalled = {false};
		AbstractAggrFeatureEventFeatureToMaxMapFunc f = new AbstractAggrFeatureEventFeatureToMaxMapFunc() {
			@Override
			protected AggrFeatureValue calculateMapAggrFeatureValue(AggrFeatureValue aggrFeatureValue) {
				Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) aggrFeatureValue.getValue();
				Assert.assertEquals(2, featuresGroupToMax.size());
				List<String> groupByFeatureValues = new ArrayList<String>() {{
					add(feature1);
				}};
				Assert.assertEquals(max1, featuresGroupToMax.get(groupByFeatureValues).intValue());

				groupByFeatureValues = new ArrayList<String>() {{
					add(feature2);
				}};
				Assert.assertEquals(max2, featuresGroupToMax.get(groupByFeatureValues).intValue());

				calculateMapAggrFeatureValueWasCalled[0] = true;
				return null;
			}
		};

		List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = createMultipleBucketsAggrFeaturesMapList(pluckFeatureName, aggrFeature1, aggrFeature2);
		f.calculateAggrFeature(
				createAggregatedFeatureEventConf("sum_of_highest_scores_over_src_machines_vpn_hourly", pluckFeatureName),
				multipleBucketsAggrFeaturesMapList);

		Assert.assertTrue(calculateMapAggrFeatureValueWasCalled[0]);
	}
}
