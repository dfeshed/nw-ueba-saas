package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
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
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, map, new JSONObject());
	}

	@Test
	public void testCalculateAggrFeature() {
		String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

		GenericHistogram histogram1 = new GenericHistogram();
		histogram1.add("first", 1.0);
		histogram1.add("second", 2.0);
		histogram1.add("third", 3.0);

		GenericHistogram notListedHistogram = new GenericHistogram();
		notListedHistogram.add("first", 1.0);
		notListedHistogram.add("fifths", 5.0);
		notListedHistogram.add("tenth", 10.0);

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<>("feature1", histogram1),
				new ImmutablePair<>("feature2", notListedHistogram)
		);

		GenericHistogram histogram2 = new GenericHistogram();
		histogram2.add("first", 1.0);
		histogram2.add("second", 2.0);
		String newOccurrenceValue = "newOccurrence";
		histogram2.add(newOccurrenceValue, 4.0);
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

	private AggrFeatureValue createExpected(int numberOfNewOccurrences, GenericHistogram... genericHistograms) {
		AggrFeatureValue ret = new AggrFeatureValue(numberOfNewOccurrences, 0L);
		GenericHistogram sumGenericHistogram = new GenericHistogram();
		for (GenericHistogram hist : genericHistograms) {
			sumGenericHistogram.add(hist);
		}
		ret.setTotal((long)sumGenericHistogram.getTotalCount());
		return ret;
	}

	@Test
	public void testCalculateAggrFeatureWhenConfigIsNull() {
		AggrFeatureEventNumberOfNewOccurrencesFunc function = new AggrFeatureEventNumberOfNewOccurrencesFunc();
		Assert.assertNull(function.calculateAggrFeature(null, new ArrayList<>()));
	}
}
