package fortscale.aggregation.feature.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;

/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureEventNumberOfEventsFuncTest {

	private AggregatedFeatureConf createAggregatedFeatureConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureEventNumberOfEventsFunc.AGGREGATED_FEATURE_NAME_TO_SUM, list);
		return new AggregatedFeatureConf(name, map, new JSONObject());
	}

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureEventNumberOfEventsFunc.AGGREGATED_FEATURE_NAME_TO_SUM, list);
		return new AggregatedFeatureEventConf(name, "bucketConfName", 3, 1, 300, map, new JSONObject());
	}

	@Test
	public void testUpdateAggrFeature() {
		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		AggregatedFeatureConf conf = createAggregatedFeatureConf("featureName", 1);
		int actual1 = (int)function.updateAggrFeature(conf, new HashMap<String, Feature >(), new Feature("aggregatedFeatureEventTestName", 10));
		Assert.assertEquals(11, (int) actual1);
	}

	@Test
	public void testUpdateAggrFeatureWithNulls() {
		String aggregatedFeatureName = "aggregatedFeatureEventTestName";

		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		Object actual1 = function.updateAggrFeature(null, new HashMap<String, Feature >(), new Feature("featureName", "featureValue"));
		Assert.assertNull(actual1);
		AggregatedFeatureConf conf1 = createAggregatedFeatureConf(aggregatedFeatureName, 1);
		Feature actual2 = (Feature)function.updateAggrFeature(conf1, new HashMap<String, Feature >(), null);
		Assert.assertEquals(aggregatedFeatureName, actual2.getName());
		int valueResult = (int)actual2.getValue();
		Assert.assertEquals(1, valueResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAggrFeatureWrongFeatureValueType() {
		AggregatedFeatureConf conf = createAggregatedFeatureConf("", 1);
		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		@SuppressWarnings("unused")
		Object actual1 = function.updateAggrFeature(conf, new HashMap<String, Feature >(), new Feature("featureName", "NOT_INTEGER_VALUE"));
	}

	@Test
	public void testCalculateAggrFeature() {
		String featureNameToCount = "featureToCount";


		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature(featureNameToCount, 1));
		bucket1FeatureMap.put("feature2", new Feature("feature2", 8));

		Map<String, Feature> bucket2FeatureMap = new HashMap<>();
		bucket2FeatureMap.put("feature1", new Feature(featureNameToCount, 12));
		bucket2FeatureMap.put("feature2", new Feature("feature2", 42));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual1.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(13, aggrFeatureValue.getValue());
	}

	@Test
	public void testCalculateAggrFeatureConfiguredFeatureNameNotInBuckets() {

		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("counter1", new Feature("counter1", 1));
		bucket1FeatureMap.put("counter2", new Feature("counter2", 8));

		Map<String, Feature> bucket2FeatureMap = new HashMap<>();
		bucket2FeatureMap.put("counter1", new Feature("counter1", 13));
		bucket2FeatureMap.put("counter2", new Feature("counter2", 42));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf("NonExistingFeature", 1);
		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0, aggrFeatureValue.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAggrFeatureHistogramFeatureType() {
		GenericHistogram histogram1 = new GenericHistogram();
		histogram1.add("first", 1.0);
		histogram1.add("second", 2.0);
		histogram1.add("third", 3.0);
		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature("counter1", histogram1));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf("feature1", 1);
		AggrFeatureEventNumberOfEventsFunc function = new AggrFeatureEventNumberOfEventsFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0, aggrFeatureValue.getValue());
	}
}
