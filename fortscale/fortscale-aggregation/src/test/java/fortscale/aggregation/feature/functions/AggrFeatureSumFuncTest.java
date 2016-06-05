package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.datastructures.GenericHistogram;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


/**
 * Created by orend on 26/07/2015.
 */
public class AggrFeatureSumFuncTest {

	private AggregatedFeatureConf createAggregatedFeatureConf(String name) {
		return new AggregatedFeatureConf(name, new HashMap<String, List<String>>(), new JSONObject());
	}

	private AggregatedFeatureConf createAggregatedFeatureConf(String name, String featureToSum) {
		AggregatedFeatureConf conf = createAggregatedFeatureConf(name);
		conf.getFeatureNamesMap().put("sum", Collections.singletonList(featureToSum));
		return conf;
	}

	private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			list.add(String.format("feature%d", i));
		}
		Map<String, List<String>> map = new HashMap<>();
		map.put("sum", list);
		return new AggregatedFeatureEventConf(name, "F", "bucketConfName", 3, 1, 300, "HIGHEST_SCORE",  map, new JSONObject());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateAggrFeatureWrongFeatureValueType() {
		AggregatedFeatureConf conf = createAggregatedFeatureConf("featureName");
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		function.updateAggrFeature(conf, new HashMap<String, Feature>(), new Feature("featureName", "NOT_INTEGER_VALUE"));
	}

	@Test
	public void testUpdateAggrFeatureWhenCounting() {
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		AggregatedFeatureConf conf = createAggregatedFeatureConf("featureName");
		AggrFeatureValue actual1 = (AggrFeatureValue)function.updateAggrFeature(conf, new HashMap<String, Feature >(), new Feature("aggregatedFeatureEventTestName", new AggrFeatureValue(10D, 10L)));
		Assert.assertEquals(11D, actual1.getValue());
	}

	@Test
	public void testUpdateAggrFeatureWhenSummingSpecificFeature() {
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		String featureNameToSum = "score";
		AggregatedFeatureConf conf = createAggregatedFeatureConf("featureName", featureNameToSum);
		double sum = 10;
		double score = 50;
		Map<String, Feature> features = AggrFeatureTestUtils.createFeatureMap(new ImmutablePair<String, Object>(featureNameToSum, score));
		AggrFeatureValue actual = (AggrFeatureValue)function.updateAggrFeature(conf, features, new Feature("aggregatedFeatureEventTestName", new AggrFeatureValue(sum, 2L)));
		Assert.assertEquals(sum + score, actual.getValue());
	}

	@Test
	public void testCalculateAggrFeature() {
		String featureNameToCount = "featureToCount";


		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", new AggrFeatureValue(1D, 1L)),
				new ImmutablePair<String, Object>("feature2", new AggrFeatureValue(8D, 8L))
		);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", new AggrFeatureValue(12D, 12L)),
				new ImmutablePair<String, Object>("feature2", new AggrFeatureValue(42D, 42L))
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf(featureNameToCount, 1);
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		Assert.assertEquals(featureNameToCount, actual1.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(13D, aggrFeatureValue.getValue());
	}

	@Test
	public void testCalculateAggrFeatureConfiguredFeatureNameNotInBuckets() {

		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("counter1", new AggrFeatureValue(1D, 1L)),
				new ImmutablePair<String, Object>("counter2", new AggrFeatureValue(8D, 8L))
		);

		Map<String, Feature> bucket2FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("counter1", new AggrFeatureValue(13D, 13L)),
				new ImmutablePair<String, Object>("counter2", new AggrFeatureValue(42D, 42L))
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);
		listOfFeatureMaps.add(bucket2FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf("NonExistingFeature", 1);
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0D, aggrFeatureValue.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAggrFeatureHistogramFeatureType() {
		GenericHistogram histogram1 = new GenericHistogram();
		histogram1.add("first", 1.0);
		histogram1.add("second", 2.0);
		histogram1.add("third", 3.0);
		Map<String, Feature> bucket1FeatureMap = AggrFeatureTestUtils.createFeatureMap(
				new ImmutablePair<String, Object>("feature1", histogram1)
		);

		List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
		listOfFeatureMaps.add(bucket1FeatureMap);

		AggregatedFeatureEventConf conf = createAggregatedFeatureEventConf("feature1", 1);
		AggrFeatureSumFunc function = new AggrFeatureSumFunc();
		Feature actual1 = function.calculateAggrFeature(conf, listOfFeatureMaps);
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual1.getValue();
		Assert.assertEquals(0, aggrFeatureValue.getValue());
	}
}
