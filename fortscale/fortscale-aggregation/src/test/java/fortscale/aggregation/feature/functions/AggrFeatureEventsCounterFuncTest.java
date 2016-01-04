package fortscale.aggregation.feature.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.FeatureNumericValue;
import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.util.GenericHistogram;

/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureEventsCounterFuncTest {

	private AggregatedFeatureConf createAggregatedFeatureConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureEventsCounterFunc.AGGREGATED_FEATURE_NAME_TO_SUM, list);
		return new AggregatedFeatureConf(name, map, new JSONObject());
	}

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureEventsCounterFunc.AGGREGATED_FEATURE_NAME_TO_SUM, list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "HIGHEST_SCORE",  map, new JSONObject());
	}

	@Test
	public void testUpdateAggrFeature() {
		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		AggregatedFeatureConf conf = createAggregatedFeatureConf("featureName", 1);
		FeatureNumericValue actual1 = (FeatureNumericValue)function.updateAggrFeature(conf, new HashMap<String, Feature >(), new Feature("aggregatedFeatureEventTestName", 10));
		Assert.assertEquals(11, (int) actual1.getValue().intValue());
	}

	@Test
	public void testUpdateAggrFeatureWithNulls() {
		String aggregatedFeatureName = "aggregatedFeatureEventTestName";

		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		Object actual1 = function.updateAggrFeature(null, new HashMap<String, Feature>(), new Feature("featureName", "featureValue"));
		Assert.assertNull(actual1);
		AggregatedFeatureConf conf1 = createAggregatedFeatureConf(aggregatedFeatureName, 1);
		FeatureNumericValue actual2 = (FeatureNumericValue)function.updateAggrFeature(conf1, new HashMap<String, Feature>(), null);
		Assert.assertEquals(1, actual2.getValue().intValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAggrFeatureWrongFeatureValueType() {
		AggregatedFeatureConf conf = createAggregatedFeatureConf("", 1);
		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		@SuppressWarnings("unused")
		Object actual1 = function.updateAggrFeature(conf, new HashMap<String, Feature >(), new Feature("featureName", "NOT_INTEGER_VALUE"));
	}

	@Test
	public void testCalculateAggrFeature() {
		String featureNameToCount = "featureToCount";


		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature("feature1", 1L));
		bucket1FeatureMap.put("feature2", new Feature("feature2", 8L));

		Map<String, Feature> bucket2FeatureMap = new HashMap<>();
		bucket2FeatureMap.put("feature1", new Feature("feature1", 12));
		bucket2FeatureMap.put("feature2", new Feature("feature2", 42));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual1.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(13L, aggrFeatureValue.getValue());
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
		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0L, aggrFeatureValue.getValue());
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
		AggrFeatureEventsCounterFunc function = new AggrFeatureEventsCounterFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0, aggrFeatureValue.getValue());
	}
}
