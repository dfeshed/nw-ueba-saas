package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

/**
 * @author Lior Govrin
 */
public class AggrFeatureEventHistogramKeyValueFuncTest {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void should_return_null_when_there_are_no_histograms() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				"success"
		);

		GenericHistogram genericHistogram = new GenericHistogram();
		genericHistogram.add("success", 1.0);
		Feature feature = new Feature("another_aggregated_feature", genericHistogram);

		List<Map<String, Feature>> list = asList(
				singletonMap("another_aggregated_feature", feature),
				singletonMap("my_aggregated_feature", null),
				emptyMap()
		);

		Assert.assertNull(deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list));
	}

	@Test
	public void should_return_null_when_all_histograms_are_empty() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				"failure"
		);

		GenericHistogram genericHistogram1 = new GenericHistogram();
		Feature feature1 = new Feature("my_aggregated_feature", genericHistogram1);
		GenericHistogram genericHistogram2 = new GenericHistogram();
		Feature feature2 = new Feature("my_aggregated_feature", genericHistogram2);

		List<Map<String, Feature>> list = asList(
				singletonMap("my_aggregated_feature", feature1),
				singletonMap("my_aggregated_feature", feature2)
		);

		Assert.assertNull(deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list));
	}

	@Test
	public void should_return_zero_when_histograms_are_not_empty_but_do_not_contain_the_required_key() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				"success"
		);

		GenericHistogram genericHistogram = new GenericHistogram();
		genericHistogram.add("failure", 1.0);
		Feature feature = new Feature("my_aggregated_feature", genericHistogram);

		List<Map<String, Feature>> list = singletonList(singletonMap("my_aggregated_feature", feature));

		Feature actual = deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list);
		Assert.assertEquals("my_aggregated_feature_event", actual.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual.getValue();
		Assert.assertEquals(0.0, aggrFeatureValue.getValue());
		Assert.assertEquals(new Long(1), aggrFeatureValue.getTotal());
	}

	@Test
	public void should_return_the_counter_of_the_required_key_when_it_is_zero() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				"failure"
		);

		GenericHistogram genericHistogram1 = new GenericHistogram();
		genericHistogram1.add("success", 1.0);
		genericHistogram1.add("failure", 0.0);
		Feature feature1 = new Feature("my_aggregated_feature", genericHistogram1);
		GenericHistogram genericHistogram2 = new GenericHistogram();
		genericHistogram2.add("success", 2.0);
		genericHistogram2.add("failure", 0.0);
		Feature feature2 = new Feature("my_aggregated_feature", genericHistogram2);
		GenericHistogram genericHistogram3 = new GenericHistogram();
		genericHistogram3.add("success", 3.0);
		genericHistogram3.add("failure", 0.0);
		Feature feature3 = new Feature("my_aggregated_feature", genericHistogram3);

		List<Map<String, Feature>> list = asList(
				singletonMap("my_aggregated_feature", feature1),
				singletonMap("my_aggregated_feature", feature2),
				singletonMap("my_aggregated_feature", feature3)
		);

		Feature actual = deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list);
		Assert.assertEquals("my_aggregated_feature_event", actual.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual.getValue();
		Assert.assertEquals(0.0, aggrFeatureValue.getValue());
		Assert.assertEquals(new Long(6), aggrFeatureValue.getTotal());
	}

	@Test
	public void should_return_the_counter_of_the_required_key_when_it_is_a_positive_number() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				"success"
		);

		GenericHistogram genericHistogram1 = new GenericHistogram();
		genericHistogram1.add("success", 1.0);
		Feature feature1 = new Feature("my_aggregated_feature", genericHistogram1);
		GenericHistogram genericHistogram2 = new GenericHistogram();
		genericHistogram2.add("success", 2.0);
		Feature feature2 = new Feature("my_aggregated_feature", genericHistogram2);

		List<Map<String, Feature>> list = asList(
				singletonMap("my_aggregated_feature", feature1),
				singletonMap("my_aggregated_feature", feature2)
		);

		Feature actual = deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list);
		Assert.assertEquals("my_aggregated_feature_event", actual.getName());
		AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)actual.getValue();
		Assert.assertEquals(3.0, aggrFeatureValue.getValue());
		Assert.assertEquals(new Long(3), aggrFeatureValue.getTotal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_when_the_required_key_is_missing() throws Exception {
		AggregatedFeatureEventConf conf = getAggregatedFeatureEventConf(
				"my_aggregated_feature_event",
				"my_aggregated_feature",
				null
		);

		GenericHistogram genericHistogram = new GenericHistogram();
		genericHistogram.add("failure", 1.0);
		Feature feature = new Feature("my_aggregated_feature", genericHistogram);

		List<Map<String, Feature>> list = singletonList(singletonMap("my_aggregated_feature", feature));

		deserializeAggregatedFeatureEventFunction(conf).calculateAggrFeature(conf, list);
	}

	private static AggregatedFeatureEventConf getAggregatedFeatureEventConf(
			String aggregatedFeatureEventName,
			String aggregatedFeatureName,
			String aggregatedFeatureEventFunctionKey) {

		return new AggregatedFeatureEventConf(
				aggregatedFeatureEventName,
				AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE,
				"test_feature_bucket_conf",
				1,
				1,
				300,
				"test_evidence_filter_strategy",
				singletonMap(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, singletonList(aggregatedFeatureName)),
				getAggregatedFeatureEventFunctionJsonObject(aggregatedFeatureEventFunctionKey)
		);
	}

	private static JSONObject getAggregatedFeatureEventFunctionJsonObject(String key) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", AggrFeatureEventHistogramKeyValueFunc.AGGR_FEATURE_FUNCTION_TYPE);
		jsonObject.put(AggrFeatureEventHistogramKeyValueFunc.KEY_FIELD_NAME, key);
		return jsonObject;
	}

	private static IAggrFeatureEventFunction deserializeAggregatedFeatureEventFunction(
			AggregatedFeatureEventConf aggregatedFeatureEventConf) throws Exception {

		String jsonString = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction().toJSONString();
		return objectMapper.readValue(jsonString, IAggrFeatureEventFunction.class);
	}
}
