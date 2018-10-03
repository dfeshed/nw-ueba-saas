package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author Oren Dor
 * @author Lior Govrin
 */
public class AggrFeatureDistinctValuesCounterFuncTest {

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, map, new JSONObject());
	}

	private AggrFeatureValue createExpected(Long numberOfDistinctValues, MultiKeyHistogram... multiKeyHistograms) {
		AggrFeatureValue ret = new AggrFeatureValue(numberOfDistinctValues, 0L);
		MultiKeyHistogram sumMultiKeyHistogram = new MultiKeyHistogram();
		for (MultiKeyHistogram hist : multiKeyHistograms) {
            sumMultiKeyHistogram.add(hist);
		}
        sumMultiKeyHistogram.remove(new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE));
		ret.setTotal((long)sumMultiKeyHistogram.getTotal());
		return ret;
	}

	@Test
	public void testCalculateAggrFeature() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		Map<MultiKeyFeature, Double> histogram1 = new HashMap<>();
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("secondName","secondValue"),2.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirdName","thirdValue"),3.0);
		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram(histogram1, 0.0);

		Map<MultiKeyFeature, Double> notListedHistogram = new HashMap<>();
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram(notListedHistogram, 0.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram1),
				new ImmutablePair<>("feature2", multiKeyNotListedHistogram)
		);

		Map<MultiKeyFeature, Double> histogram2 = new HashMap<>();
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("seventeenthName","seventeenthValue"),17.0);
		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram(histogram2, 0.0);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram2),
				new ImmutablePair<>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(6L, multiKeyHistogram1, multiKeyHistogram2);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
	}

	@Test
	public void testCalculateAggrFeatureWithNaValues() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		Map<MultiKeyFeature, Double> histogram1 = new HashMap<>();
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("secondName","secondValue"),2.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirdName","thirdValue"),3.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NaName",AggGenericNAFeatureValues.NOT_AVAILABLE),3.0);
		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram(histogram1, 0.0);

		Map<MultiKeyFeature, Double> notListedHistogram = new HashMap<>();
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram(notListedHistogram, 0.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram1),
				new ImmutablePair<>("feature2", multiKeyNotListedHistogram)
		);

		Map<MultiKeyFeature, Double> histogram2 = new HashMap<>();
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("seventeenthName","seventeenthValue"),17.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NaName",AggGenericNAFeatureValues.NOT_AVAILABLE),17.0);
		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram(histogram2, 0.0);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram2),
				new ImmutablePair<>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();
		function.setRemoveNA(true);
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(6L, multiKeyHistogram1, multiKeyHistogram2);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
		Assert.assertEquals(expectedAggrFeatureValue.getTotal(), actualAggrFeatureValue.getTotal());
	}

	@Test
	public void testCalculateAggrFeatureWhenHistogramsAreEmpty() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram();
		Map<MultiKeyFeature, Double> notListedHistogram = new HashMap<>();
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram(notListedHistogram, 0.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram1),
				new ImmutablePair<>("feature2", multiKeyNotListedHistogram)
		);

		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram();
		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram2),
				new ImmutablePair<>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNull(actual1);
	}

	@Test
	public void testCalculateAggrFeatureWhenFeatureDoesNotExist() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		Map<MultiKeyFeature, Double> notListedHistogram = new HashMap<>();
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram(notListedHistogram, 0.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature2", multiKeyNotListedHistogram)
		);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNull(actual1);
	}

	@Test
	public void testCalculateAggrFeatureWhenConfigIsNull() {
		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();
		Assert.assertNull(function.calculateAggrFeature(null, new ArrayList<>()));
	}

	@Test
	public void testCalculateAggrFeatureWithFilteredMultiKeys(){
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		MultiKeyHistogram multiKeyHistogram = createMultiKeyHistogram();

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();

		Set<Map<String, String>> keys = new HashSet<>();
		Map<String, String> featureNameToValueKey = new HashMap<>();
		featureNameToValueKey.put("result","SUCCESS");
		keys.add(featureNameToValueKey);
		function.setKeys(keys);

		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(2L, multiKeyHistogram);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
	}

	/**
	 * Test zero AggrFeature creation, where no key was met
	 */
	@Test
	public void testCalculateAggrFeatureWithNoAppropriateKey(){
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		MultiKeyHistogram multiKeyHistogram = createMultiKeyHistogram();

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", multiKeyHistogram)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);

		AggrFeatureDistinctValuesCounterFunc function = new AggrFeatureDistinctValuesCounterFunc();

		Set<Map<String, String>> keys = new HashSet<>();
		Map<String, String> featureNameToValueKey = new HashMap<>();
		featureNameToValueKey.put("result","noValue");
		keys.add(featureNameToValueKey);
		function.setKeys(keys);

		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(0L, multiKeyHistogram);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
	}

	/**
	 * Help func that create MultiKeyHistogram
	 * @return MultiKeyHistogram
	 */
	private MultiKeyHistogram createMultiKeyHistogram(){
		Map<MultiKeyFeature, Double> histogram1 = new HashMap<>();
		Map<String, String> featureNameToValue1 = new HashMap<>();
		featureNameToValue1.put("operationType","open");
		featureNameToValue1.put("result","SUCCESS");
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1),10.0);

		Map<String, String> featureNameToValue3 = new HashMap<>();
		featureNameToValue3.put("operationType","open");
		featureNameToValue3.put("result","FAILURE");
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue3),2.0);

		Map<String, String> featureNameToValue4 = new HashMap<>();
		featureNameToValue4.put("operationType","close");
		featureNameToValue4.put("result","SUCCESS");
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue4),3.0);
		return new MultiKeyHistogram(histogram1, 0.0);
	}
}
