package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureKeyMaxValueFuncTest {

	private AggregatedFeatureConf createAggrFeatureConf(int num) {
		List<String> featureNames = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			featureNames.add(String.format("feature%d", i));
		}
		Map<String, List<String>> featureNamesMap = new HashMap<>();
		featureNamesMap.put(AggrFeatureKeyMaxValueFunc.GROUP_BY_FIELD_NAME, featureNames);
		return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
	}

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureKeyMaxValueFunc.GROUP_BY_FIELD_NAME, list);
		return new AggregatedFeatureEventConf(name, "bucketConfName", 3, 1, 300, map, new JSONObject());
	}

	@Test
	public void testUpdateAggrFeature() {
		JSONObject aggrFeatureValue = new JSONObject();
		aggrFeatureValue.put("feature1", 1);
		aggrFeatureValue.put("feature2", 90);
		aggrFeatureValue.put("newFeature", 10);

		Map<String, Feature> featureMap = new HashMap<>();
		featureMap.put("feature1", new Feature("feature1", 2));
		featureMap.put("feature2", new Feature("feature2", 2));
		featureMap.put("not relevant", new Feature("not relevant", 2));

		Feature aggrFeature = new Feature("MyAggrFeature", aggrFeatureValue);
		AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();

		Object value = function.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

		Assert.assertTrue(value instanceof JSONObject);
		JSONObject actual = (JSONObject)value;
		Assert.assertEquals(3, actual.size());
		Assert.assertEquals(2, actual.get("feature1"));
		Assert.assertEquals(90, actual.get("feature2"));
	}

	@Test
	public void testUpdateWithNullAggrFeatureValue() {
		Map<String, Feature> featureMap = new HashMap<>();
		featureMap.put("feature1", new Feature("feature1", 2));
		featureMap.put("feature2", new Feature("feature2", 2));
		featureMap.put("not relevant", new Feature("not relevant", 2));

		Feature aggrFeature = new Feature("MyAggrFeature", null);
		AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();

		function.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);
		Assert.assertTrue(aggrFeature.getValue() instanceof JSONObject);
		Assert.assertEquals(0, ((JSONObject)aggrFeature.getValue()).size());
	}

	@Test
	public void testUpdateWithNullAggrFeatureConf() {
		JSONObject aggrFeatureValue = new JSONObject();
		aggrFeatureValue.put("feature1", 1);
		aggrFeatureValue.put("feature2", 90);
		aggrFeatureValue.put("newFeature", 10);

		Map<String, Feature> featureMap = new HashMap<>();
		featureMap.put("feature1", new Feature("feature1", 2));
		featureMap.put("feature2", new Feature("feature2", 2));
		featureMap.put("not relevant", new Feature("not relevant", 2));

		Feature aggrFeature = new Feature("MyAggrFeature", aggrFeatureValue);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();

		Object value =function.updateAggrFeature(null, featureMap, aggrFeature);

		Assert.assertNull(value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithWrongAggrFeatureValueType() {
		Map<String, Feature> featureMap1 = new HashMap<>();
		featureMap1.put("feature1", new Feature("feature1", 2));
		AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
		String str = "I'm a string, not JSONObject";
		Feature aggrFeature = new Feature("MyAggrFeature", str);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();
		function.updateAggrFeature(aggrFuncConf, featureMap1, aggrFeature);
	}

	@Test
	public void testUpdateWithNullAggrFeature() {
		Map<String, Feature> featureMap = new HashMap<>();
		featureMap.put("feature1", new Feature("feature1", 2));
		featureMap.put("feature2", new Feature("feature2", 2));
		featureMap.put("not relevant", new Feature("not relevant", 2));
		AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();

		Object value = function.updateAggrFeature(aggrFuncConf, featureMap, null);

		Assert.assertNull(value);
	}

	@Test
	public void testUpdateWithNullFeatures() {
		JSONObject aggrFeatureValue = new JSONObject();
		aggrFeatureValue.put("feature1", 1);
		aggrFeatureValue.put("feature2", 90);
		aggrFeatureValue.put("newFeature", 10);

		Feature aggrFeature = new Feature("MyAggrFeature", aggrFeatureValue);
		AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();

		Object value = function.updateAggrFeature(aggrFuncConf, null, aggrFeature);

		Assert.assertTrue(value instanceof JSONObject);
		JSONObject actual = (JSONObject)value;
		Assert.assertEquals(3, actual.size());
		Assert.assertEquals(1, actual.get("feature1"));
		Assert.assertEquals(90, actual.get("feature2"));
	}

	@Test
	public void testCalculateAggrFeature() {
		String confName = "testCalculateAggrFeature";

		Map<String, Feature> map1 = new HashMap<>();
		map1.put("feature1", new Feature("feature1", 2));
		map1.put("feature2", new Feature("feature2", 2));
		map1.put("not relevant", new Feature("not relevant", 2));

		Map<String, Feature> map2 = new HashMap<>();
		map2.put("feature1", new Feature("feature1", 1));
		map2.put("feature2", new Feature("feature2", 3));

		Map<String, Feature> map3 = new HashMap<>();
		map3.put("feature1", new Feature("feature1", 0));
		map3.put("feature2", new Feature("feature2", 0));

		List<Map<String, Feature>> listOfMaps = new ArrayList<>();
		listOfMaps.add(map1);
		listOfMaps.add(map2);
		listOfMaps.add(map3);

		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();
		Feature actual = function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 2), listOfMaps);

		Assert.assertEquals("feature2", actual.getName());
		Assert.assertEquals(3, actual.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAggrFeatureWithANullAggregatedFeatureValue() {
		String confName = "testCalculateAggrFeature";

		Map<String, Feature> map1 = new HashMap<>();
		map1.put("feature1", new Feature("feature1", 2));
		map1.put("feature2", new Feature("feature2", 2));
		map1.put("not relevant", new Feature("not relevant", 2));

		Map<String, Feature> map2 = new HashMap<>();
		map2.put("feature1", new Feature("feature1", null));
		map2.put("feature2", new Feature("feature2", 3));

		List<Map<String, Feature>> listOfMaps = new ArrayList<>();
		listOfMaps.add(map1);
		listOfMaps.add(map2);

		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();
		function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 2), listOfMaps);
	}

	@Test
	public void testCalculateAggrFeatureWithNullAggregatedFeatureEventConf() {
		Map<String, Feature> map = new HashMap<>();
		map.put("feature1", new Feature("feature1", 1));

		List<Map<String, Feature>> listOfMaps = new ArrayList<>();
		listOfMaps.add(map);

		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();
		Assert.assertNull(function.calculateAggrFeature(null, listOfMaps));
	}

	@Test
	public void testCalculateAggrFeatureWithNullAggregatedFeaturesMapList() {
		String confName = "testCalculateAggrFeatureWithNullAggregatedFeaturesMapList";

		AggrFeatureKeyMaxValueFunc function = new AggrFeatureKeyMaxValueFunc();
		Assert.assertNull(function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 3), null));
	}
}
