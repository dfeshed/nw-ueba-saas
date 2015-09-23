package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.FeatureNumericValue;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;
import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.mockito.Matchers.anyString;

/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureEventNumberOfNewOccurencesFuncTest {
	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "AnomalyAggregatedEvent", "HIGHEST_SCORE", map, new JSONObject());
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

		Map<String, Feature> bucket1FeatureMap = new HashMap<>();
		bucket1FeatureMap.put("feature1", new Feature("feature1", histogram1));
		bucket1FeatureMap.put("feature2", new Feature("feature2", notListedHistogram));

		GenericHistogram histogram2 = new GenericHistogram();
		histogram2.add("first", 1.0);
		histogram2.add("second", 2.0);
		String newOccurenceValue = "newOccurence";
		histogram2.add(newOccurenceValue, 4.0);
		Map<String, Feature> bucket2FeatureMap = new HashMap<>();
		bucket2FeatureMap.put("feature1", new Feature("feature1", histogram2));
		bucket2FeatureMap.put("feature2", new Feature("feature2", new FeatureNumericValue(42)));

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggrFeatureEventNumberOfNewOccurencesFunc function = new AggrFeatureEventNumberOfNewOccurencesFunc();
		function.setIncludeValues(true);
		Feature actual1 = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
		Assert.assertNotNull(actual1);
		Assert.assertEquals(aggregatedFeatureEventName, actual1.getName());
		Assert.assertTrue(actual1.getValue() instanceof AggrFeatureValue);
		AggrFeatureValue actualAggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		AggrFeatureValue expectedAggrFeatureValue = createExpected(1, newOccurenceValue, histogram2);
		Assert.assertEquals(expectedAggrFeatureValue.getValue(), actualAggrFeatureValue.getValue());
		Assert.assertTrue(actualAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES) instanceof Set);
		Assert.assertEquals(((Set<?>)expectedAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES)).size(), 
				((Set<?>)actualAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES)).size());
		Assert.assertEquals(((Set<?>)expectedAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES)).toArray()[0], 
				((Set<?>)actualAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES)).toArray()[0]);
		Assert.assertEquals(expectedAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES), 
				actualAggrFeatureValue.getAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES));
		Assert.assertEquals(expectedAggrFeatureValue,actualAggrFeatureValue);
	}
	
	private AggrFeatureValue createExpected(int numberOfNewOccurences, String newOccurenceValue, GenericHistogram ...genericHistograms){
		AggrFeatureValue ret = new AggrFeatureValue(numberOfNewOccurences, 0L);
		GenericHistogram sumGenericHistogram = new GenericHistogram();
		for(GenericHistogram hist: genericHistograms){
			sumGenericHistogram.add(hist);
		}
		ret.setTotal((long) sumGenericHistogram.getTotalCount());
		
		Set<String> newOccurencesSet = new HashSet<>();
		newOccurencesSet.add(newOccurenceValue);
		ret.putAdditionalInformation(AggrFeatureEventNumberOfNewOccurencesFunc.NEW_OCCURENCES_VALUES, newOccurencesSet);
		return ret;
	}

	@Test
	public void testCalculateAggrFeatureWhenConfigIsNull() {
		AggrFeatureEventNumberOfNewOccurencesFunc function = new AggrFeatureEventNumberOfNewOccurencesFunc();
		Assert.assertNull(function.calculateAggrFeature(null, new ArrayList<Map<String, Feature>>()));
	}
}
