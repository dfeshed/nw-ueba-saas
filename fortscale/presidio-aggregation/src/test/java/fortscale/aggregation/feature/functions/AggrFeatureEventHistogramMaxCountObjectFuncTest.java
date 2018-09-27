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
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram1),
				new ImmutablePair<String, Object>("feature2", multiKeyNotListedHistogram)
		);

		Map<MultiKeyFeature, Double> histogram2 = new HashMap<>();
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		MultiKeyFeature maxMultiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature(maxHistogramKey + "Name",maxHistogramKey);
		histogram2.put(maxMultiKeyFeature,17.0);
		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram(histogram2, 0.0);

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

		Map<MultiKeyFeature, Double> histogram1 = new HashMap<>();
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("secondName","secondValue"),2.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirdName","thirdValue"),3.0);
		histogram1.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NAName",AggGenericNAFeatureValues.NOT_AVAILABLE),3.0);
		MultiKeyHistogram multiKeyHistogram1 = new MultiKeyHistogram(histogram1, 0.0);

		Map<MultiKeyFeature, Double> notListedHistogram = new HashMap<>();
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		notListedHistogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram multiKeyNotListedHistogram = new MultiKeyHistogram(notListedHistogram, 0.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", multiKeyHistogram1),
				new ImmutablePair<String, Object>("feature2", multiKeyNotListedHistogram)
		);

		Map<MultiKeyFeature, Double> histogram2 = new HashMap<>();
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("eleventhName","eleventhValue"),11.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("thirteenthName","thirteenthValue"),13.0);
		MultiKeyFeature maxMultiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature(maxHistogramKey + "Name",maxHistogramKey);
		histogram2.put(maxMultiKeyFeature,17.0);
		histogram2.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("NAName",AggGenericNAFeatureValues.NOT_AVAILABLE),30.0);
		MultiKeyHistogram multiKeyHistogram2 = new MultiKeyHistogram(histogram2, 0.0);

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

		Map<MultiKeyFeature, Double> histogram = new HashMap<>();
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram notListedMultiKeyHistogram = new MultiKeyHistogram(histogram, 16.0);

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
		Map<MultiKeyFeature, Double> histogram = new HashMap<>();
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("firstName","firstValue"),1.0);
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("fifthsName","fifthsValue"),5.0);
		histogram.put(AggrFeatureTestUtils.createMultiKeyFeatureWithOneFeature("tenthName","tenthValue"),10.0);
		MultiKeyHistogram notListedMultiKeyHistogram = new MultiKeyHistogram(histogram, 16.0);

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
