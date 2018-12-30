package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

/**
 * @author Oren Dor
 * @author Lior Govrin
 */
public class AggrFeatureEventNumberOfNewOccurrencesFuncTest {
	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, map, Mockito.mock(IAggrFeatureEventFunction.class));
	}

	@Test
	public void testCalculateAggrFeature() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		MultiKeyHistogram histogram1 = new MultiKeyHistogram();
		MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
		multiKeyFeature1.add("firstFeatureName", "firstFeatureValue");
		MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature();
		multiKeyFeature2.add("secondFeatureName", "secondFeatureValue");
		MultiKeyFeature multiKeyFeature3 = new MultiKeyFeature();
		multiKeyFeature3.add("thirdFeatureName", "thirdFeatureValue");
		histogram1.set(multiKeyFeature1, 1.0);
		histogram1.set(multiKeyFeature2, 2.0);
		histogram1.set(multiKeyFeature3, 3.0);

		MultiKeyHistogram notListedHistogram = new MultiKeyHistogram();
		MultiKeyFeature multiKeyFeature4 = new MultiKeyFeature();
		multiKeyFeature4.add("fifthsFeatureName", "fifthsFeatureValue");
		MultiKeyFeature multiKeyFeature5 = new MultiKeyFeature();
		multiKeyFeature5.add("tenthFeatureName", "tenthFeatureValue");
		notListedHistogram.set(multiKeyFeature1, 1.0);
		notListedHistogram.set(multiKeyFeature2, 5.0);
		notListedHistogram.set(multiKeyFeature3, 10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", histogram1),
				new ImmutablePair<>("feature2", notListedHistogram)
		);

		MultiKeyHistogram histogram2 = new MultiKeyHistogram();
		histogram2.set(multiKeyFeature1, 1.0);
		histogram2.set(multiKeyFeature2, 2.0);
		MultiKeyFeature multiKeyFeatureNewOccurence = new MultiKeyFeature();
		multiKeyFeatureNewOccurence.add("newOccurrenceFeatureName", "newOccurrenceFeatureValue");
		histogram2.set(multiKeyFeatureNewOccurence, 4.0);
		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", histogram2),
				new ImmutablePair<>("feature2", 42)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventNumberOfNewOccurrencesFunc function = new AggrFeatureEventNumberOfNewOccurrencesFunc();
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(1, histogram2);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
		Assert.assertEquals(expectedAggrFeatureValue, actualAggrFeatureValue);
	}

	private AggrFeatureValue createExpected(int numberOfNewOccurrences, MultiKeyHistogram... multiKeyHistograms) {
		AggrFeatureValue ret = new AggrFeatureValue(numberOfNewOccurrences);
		MultiKeyHistogram sumMultiKeyHistogram = new MultiKeyHistogram();
		for (MultiKeyHistogram hist : multiKeyHistograms) {
			sumMultiKeyHistogram.add(hist, new HashSet<>());
		}
		return ret;
	}

	@Test
	public void testCalculateAggrFeatureWhenConfigIsNull() {
		AggrFeatureEventNumberOfNewOccurrencesFunc function = new AggrFeatureEventNumberOfNewOccurrencesFunc();
		Assert.assertNull(function.calculateAggrFeature(null, new ArrayList<>()));
	}
}
