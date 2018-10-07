package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureEventHistogramMaxCountObjectFuncTest {

	private AggrFeatureValue createExpected(MultiKeyFeature maxHistogramKey, MultiKeyHistogram ...multiKeyHistograms){
		return createExpected(maxHistogramKey, true, multiKeyHistograms);
	}

	private AggrFeatureValue createExpected(MultiKeyFeature maxHistogramKey, boolean removeNa, MultiKeyHistogram ...multiKeyHistograms){
		AggrFeatureValue ret = new AggrFeatureValue(maxHistogramKey,0L);
		MultiKeyHistogram sumMultiKeyHistogram = new MultiKeyHistogram();
		for(MultiKeyHistogram hist: multiKeyHistograms){
			sumMultiKeyHistogram.add(hist);
		}
		sumMultiKeyHistogram.remove(new FeatureStringValue(AggGenericNAFeatureValues.NOT_AVAILABLE));
		ret.setTotal((long)sumMultiKeyHistogram.getTotal());
		return ret;
	}

	@Test
	public void testCalculateAggrFeature() {
		String maxHistogramKey = "hasBiggestvalue";

		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram();
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("secondName","secondValue"),2.0);
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirdName","thirdValue"),3.0);

		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram();
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram1),
				new ImmutablePair<String, Object>("feature2", multiKeyNotListedHistogram)
		);

		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram();
		multiKeyHistogram2.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		multiKeyHistogram2.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		MultiKeyFeature maxMultiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature(maxHistogramKey + "Name",maxHistogramKey);
		multiKeyHistogram2.add(maxMultiKeyFeature,17.0);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram2),
				new ImmutablePair<String, Object>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventHistogramMaxCountObjectFunc function = new AggrFeatureEventHistogramMaxCountObjectFunc();

		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		Feature actual1 = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertEquals(createExpected(maxMultiKeyFeature, multiKeyHistogram1, multiKeyHistogram2), actual1.getValue());
	}

	@Test
	public void testCalculateAggrFeatureWithNaValues() {
		String maxHistogramKey = "hasBiggestvalue";

		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram();
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("secondName","secondValue"),2.0);
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirdName","thirdValue"),3.0);
		multiKeyHistogram1.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NAName",AggGenericNAFeatureValues.NOT_AVAILABLE),3.0);

		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram();
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		multiKeyNotListedHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram1),
				new ImmutablePair<String, Object>("feature2", multiKeyNotListedHistogram)
		);

		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram();
		multiKeyHistogram2.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		multiKeyHistogram2.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		MultiKeyFeature maxMultiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature(maxHistogramKey + "Name",maxHistogramKey);
		multiKeyHistogram2.add(maxMultiKeyFeature,17.0);
		multiKeyHistogram2.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NAName",AggGenericNAFeatureValues.NOT_AVAILABLE),30.0);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram2),
				new ImmutablePair<String, Object>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventHistogramMaxCountObjectFunc function = new AggrFeatureEventHistogramMaxCountObjectFunc();
		function.setRemoveNA(true);

		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		Feature actual1 = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertEquals(createExpected(maxMultiKeyFeature, multiKeyHistogram1, multiKeyHistogram2), actual1.getValue());
	}

	@Test
	public void testCalculateAggrFeatureWhenHistogramsEmpty() {
		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram();

		MultiKeyHistogram notListedMultiKeyHistogram = new MultiKeyHistogram();
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram1),
				new ImmutablePair<String, Object>("feature2", notListedMultiKeyHistogram)
		);

		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram();
		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram2),
				new ImmutablePair<String, Object>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventHistogramMaxCountObjectFunc function = new AggrFeatureEventHistogramMaxCountObjectFunc();

		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		Feature actual1 = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNull(actual1);
	}

	@Test
	public void testCalculateAggrFeatureWhenFeatureDoesNotExist() {
		MultiKeyHistogram notListedMultiKeyHistogram = new MultiKeyHistogram();
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedMultiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature2", notListedMultiKeyHistogram)
		);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventHistogramMaxCountObjectFunc function = new AggrFeatureEventHistogramMaxCountObjectFunc();

		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";
		Feature actual1 = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNull(actual1);
	}

	@Test
	public void testCalculateAggrFeatureWhenConfigIsNull() {
		AggrFeatureEventHistogramMaxCountObjectFunc function = new AggrFeatureEventHistogramMaxCountObjectFunc();
		Assert.assertNull(function.calculateAggrFeature(null, new ArrayList<Map<String, Feature>>()));
	}
}
