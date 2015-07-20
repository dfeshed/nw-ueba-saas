package fortscale.streaming.service.aggregation.feature.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatedFeatureEventConfTest {
	// Default parameters
	private static final String NAME = "aggregatedFeatureEventConf1";
	private static final String BUCKET_CONF_NAME = "bucketConf1";
	private static final int NUMBER_OF_BUCKETS = 3;
	private static final int BUCKETS_LEAP = 1;
	private static final long WAIT_AFTER_BUCKET_CLOSE_SECONDS = 500;
	private static final String FUNCTION_TYPE = "functionType1";

	@Test
	public void configuration_should_be_deserialized_from_json_containing_event_with_list() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName1");
		aggregatedFeatureNamesList.add("aggregatedFeatureName2");
		aggregatedFeatureNamesList.add("aggregatedFeatureName3");

		Map<String, String> params = new HashMap<>();
		params.put("param1", "valueOfParam1");
		params.put("param2", "valueOfParam2");
		params.put("param3", "valueOfParam3");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		AggregatedFeatureEventConf actual = (new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);

		Assert.assertNotNull(actual);
		Assert.assertEquals(NAME, actual.getName());
		Assert.assertEquals(BUCKET_CONF_NAME, actual.getBucketConfName());
		Assert.assertEquals(NUMBER_OF_BUCKETS, actual.getNumberOfBuckets());
		Assert.assertEquals(BUCKETS_LEAP, actual.getBucketsLeap());
		Assert.assertEquals(WAIT_AFTER_BUCKET_CLOSE_SECONDS, actual.getWaitAfterBucketCloseSeconds());
		Assert.assertEquals(aggregatedFeatureEventFunction, actual.getAggregatedFeatureEventFunction());
		Assert.assertEquals(aggregatedFeatureNamesList, actual.getAggregatedFeatureNamesList());
		Assert.assertNull(actual.getAggregatedFeatureNamesMap());
		Assert.assertNull(actual.getBucketConf());
	}

	@Test
	public void configuration_should_be_deserialized_from_json_containing_event_with_map() throws Exception {
		Map<String, String> aggregatedFeatureNamesMap = new HashMap<>();
		aggregatedFeatureNamesMap.put("functionFieldName1", "aggregatedFeatureName1");
		aggregatedFeatureNamesMap.put("functionFieldName2", "aggregatedFeatureName2");
		aggregatedFeatureNamesMap.put("functionFieldName3", "aggregatedFeatureName3");

		Map<String, String> params = new HashMap<>();
		params.put("param1", "valueOfParam1");
		params.put("param2", "valueOfParam2");
		params.put("param3", "valueOfParam3");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesMap);
		AggregatedFeatureEventConf actual = (new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);

		Assert.assertNotNull(actual);
		Assert.assertEquals(NAME, actual.getName());
		Assert.assertEquals(BUCKET_CONF_NAME, actual.getBucketConfName());
		Assert.assertEquals(NUMBER_OF_BUCKETS, actual.getNumberOfBuckets());
		Assert.assertEquals(BUCKETS_LEAP, actual.getBucketsLeap());
		Assert.assertEquals(WAIT_AFTER_BUCKET_CLOSE_SECONDS, actual.getWaitAfterBucketCloseSeconds());
		Assert.assertEquals(aggregatedFeatureEventFunction, actual.getAggregatedFeatureEventFunction());
		Assert.assertEquals(aggregatedFeatureNamesMap, actual.getAggregatedFeatureNamesMap());
		Assert.assertNull(actual.getAggregatedFeatureNamesList());
		Assert.assertNull(actual.getBucketConf());
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_both_list_and_map() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName1");
		aggregatedFeatureNamesList.add("aggregatedFeatureName2");
		aggregatedFeatureNamesList.add("aggregatedFeatureName3");

		Map<String, String> aggregatedFeatureNamesMap = new HashMap<>();
		aggregatedFeatureNamesMap.put("functionFieldName1", "aggregatedFeatureName1");
		aggregatedFeatureNamesMap.put("functionFieldName2", "aggregatedFeatureName2");
		aggregatedFeatureNamesMap.put("functionFieldName3", "aggregatedFeatureName3");

		Map<String, String> params = new HashMap<>();
		params.put("param1", "valueOfParam1");
		params.put("param2", "valueOfParam2");
		params.put("param3", "valueOfParam3");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		JSONObject json = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction);
		json.put("aggregatedFeatureNamesList", aggregatedFeatureNamesList);
		json.put("aggregatedFeatureNamesMap", aggregatedFeatureNamesMap);

		(new ObjectMapper()).readValue(json.toJSONString(), AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_name() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(null, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_bucket_conf_name() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, "", NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_number_of_buckets() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, 0, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_buckets_leap() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, 0, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_wait_after_bucket_close_seconds() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, -1, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_aggregated_feature_event_function() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		aggregatedFeatureNamesList.add("aggregatedFeatureName");

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, null, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_list() throws Exception {
		List<String> aggregatedFeatureNamesList = new ArrayList<>();
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesList);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_map() throws Exception {
		Map<String, String> aggregatedFeatureNamesMap = new HashMap<>();
		Map<String, String> params = new HashMap<>();
		params.put("param", "valueOfParam");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(NAME, BUCKET_CONF_NAME, NUMBER_OF_BUCKETS, BUCKETS_LEAP, WAIT_AFTER_BUCKET_CLOSE_SECONDS, aggregatedFeatureEventFunction, aggregatedFeatureNamesMap);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	private static JSONObject createAggregatedFeatureEventFunction(String type, Map<String, String> params) {
		JSONObject result = new JSONObject();
		result.put("type", type);
		result.put("params", params);
		return result;
	}

	private static JSONObject createAggregatedFeatureEvent(
			String name,
			String bucketConfName,
			int numberOfBuckets,
			int bucketsLeap,
			long waitAfterBucketCloseSeconds,
			JSONObject aggregatedFeatureEventFunction) {

		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("bucketConfName", bucketConfName);
		result.put("numberOfBuckets", numberOfBuckets);
		result.put("bucketsLeap", bucketsLeap);
		result.put("waitAfterBucketCloseSeconds", waitAfterBucketCloseSeconds);
		result.put("aggregatedFeatureEventFunction", aggregatedFeatureEventFunction);
		return result;
	}

	private static String createAggregatedFeatureEvent(
			String name,
			String bucketConfName,
			int numberOfBuckets,
			int bucketsLeap,
			long waitAfterBucketCloseSeconds,
			JSONObject aggregatedFeatureEventFunction,
			List<String> aggregatedFeatureNamesList) {

		JSONObject jsonObject = createAggregatedFeatureEvent(name, bucketConfName, numberOfBuckets, bucketsLeap, waitAfterBucketCloseSeconds, aggregatedFeatureEventFunction);
		jsonObject.put("aggregatedFeatureNamesList", aggregatedFeatureNamesList);
		return jsonObject.toJSONString();
	}

	private static String createAggregatedFeatureEvent(
			String name,
			String bucketConfName,
			int numberOfBuckets,
			int bucketsLeap,
			long waitAfterBucketCloseSeconds,
			JSONObject aggregatedFeatureEventFunction,
			Map<String, String> aggregatedFeatureNamesMap) {

		JSONObject jsonObject = createAggregatedFeatureEvent(name, bucketConfName, numberOfBuckets, bucketsLeap, waitAfterBucketCloseSeconds, aggregatedFeatureEventFunction);
		jsonObject.put("aggregatedFeatureNamesMap", aggregatedFeatureNamesMap);
		return jsonObject.toJSONString();
	}
}
