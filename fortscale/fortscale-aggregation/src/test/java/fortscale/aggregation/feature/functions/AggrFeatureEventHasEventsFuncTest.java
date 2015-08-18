package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orend on 13/08/2015.
 */

public class AggrFeatureEventHasEventsFuncTest {

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureEventNumberOfEventsFunc.AGGREGATED_FEATURE_NAME_TO_SUM, list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, map, new JSONObject());
	}

	@Test
	public void testCalculateAggrFeatureMultipleEvents() {
		String featureNameToCount = "featureToCount";

		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature("feature1", 1));
		bucket1FeatureMap.put("feature2", new Feature("feature2", 8));

		Map<String, Feature> bucket2FeatureMap = new HashMap<>();
		bucket2FeatureMap.put("feature1", new Feature("feature1", 12));
		bucket2FeatureMap.put("feature2", new Feature("feature2", 42));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureEventHasEventsFunc function = new AggrFeatureEventHasEventsFunc();
		Feature actual = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual.getName());
		Assert.assertEquals(new AggrFeatureValue(1, 13L), actual.getValue());
	}

	@Test
	public void testCalculateAggrFeatureNoEvents() {
		String featureNameToCount = "featureToCount";

		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		Map<String, Feature> bucket2FeatureMap = new HashMap<>();

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureEventHasEventsFunc function = new AggrFeatureEventHasEventsFunc();
		Feature actual = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual.getName());
		Assert.assertEquals(new AggrFeatureValue(0, 0L), actual.getValue());
	}

	@Test
	public void testCalculateAggrFeatureSingleEvent() {
		String featureNameToCount = "featureToCount";

		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature(featureNameToCount, 7));

		Map<String, Feature> bucket2FeatureMap = new HashMap<>();

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureEventHasEventsFunc function = new AggrFeatureEventHasEventsFunc();
		Feature actual = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual.getName());
		Assert.assertEquals(new AggrFeatureValue(1, 7L), actual.getValue());
	}
}
