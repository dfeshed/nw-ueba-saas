package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

public class AggregatedFeatureEventConfTest {
	// Default parameters
	private static final String NAME = "aggregatedFeatureEventConf1";
	private static final String TYPE = "F";
	private static final String BUCKET_CONF_NAME = "bucketConf1";
	private static final int NUMBER_OF_BUCKETS = 3;
	private static final int BUCKETS_LEAP = 1;
	private static final long WAIT_AFTER_BUCKET_CLOSE_SECONDS = 500;
	private static final String FUNCTION_TYPE = "functionType1";

	@Test
	public void configuration_should_be_deserialized_from_event_json() throws Exception {
		List<String> aggregatedFeatureNamesList1 = new ArrayList<>();
		aggregatedFeatureNamesList1.add("aggregatedFeatureName1");
		aggregatedFeatureNamesList1.add("aggregatedFeatureName2");
		aggregatedFeatureNamesList1.add("aggregatedFeatureName3");

		List<String> aggregatedFeatureNamesList2 = new ArrayList<>();
		aggregatedFeatureNamesList2.add("aggregatedFeatureName4");
		aggregatedFeatureNamesList2.add("aggregatedFeatureName5");
		aggregatedFeatureNamesList2.add("aggregatedFeatureName6");

		List<String> aggregatedFeatureNamesList3 = new ArrayList<>();
		aggregatedFeatureNamesList3.add("aggregatedFeatureName1");
		aggregatedFeatureNamesList3.add("aggregatedFeatureName4");

		// Function arguments (input)
		Map<String, List<String>> aggregatedFeatureNamesMap = new HashMap<>();
		aggregatedFeatureNamesMap.put("argument1", aggregatedFeatureNamesList1);
		aggregatedFeatureNamesMap.put("argument2", aggregatedFeatureNamesList2);
		aggregatedFeatureNamesMap.put("argument3", aggregatedFeatureNamesList3);

		Set<String> allAggregatedFeatureNames = new HashSet<>();
		allAggregatedFeatureNames.add("aggregatedFeatureName1");
		allAggregatedFeatureNames.add("aggregatedFeatureName2");
		allAggregatedFeatureNames.add("aggregatedFeatureName3");
		allAggregatedFeatureNames.add("aggregatedFeatureName4");
		allAggregatedFeatureNames.add("aggregatedFeatureName5");
		allAggregatedFeatureNames.add("aggregatedFeatureName6");

		// Function parameters (constants)
		Map<String, String> params = new HashMap<>();
		params.put("param1", "valueOfParam1");
		params.put("param2", "valueOfParam2");
		params.put("param3", "valueOfParam3");
		JSONObject aggregatedFeatureEventFunction = createAggregatedFeatureEventFunction(FUNCTION_TYPE, params);

		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				aggregatedFeatureNamesMap,
				aggregatedFeatureEventFunction);
		AggregatedFeatureEventConf actual = (new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);

		Assert.assertNotNull(actual);
		Assert.assertEquals(NAME, actual.getName());
		Assert.assertEquals(BUCKET_CONF_NAME, actual.getBucketConfName());
		Assert.assertNull(actual.getBucketConf());
		Assert.assertEquals(NUMBER_OF_BUCKETS, actual.getNumberOfBuckets());
		Assert.assertEquals(BUCKETS_LEAP, actual.getBucketsLeap());
		Assert.assertEquals(WAIT_AFTER_BUCKET_CLOSE_SECONDS, actual.getWaitAfterBucketCloseSeconds());
		Assert.assertEquals(aggregatedFeatureNamesMap, actual.getAggregatedFeatureNamesMap());
		Assert.assertEquals(allAggregatedFeatureNames, actual.getAllAggregatedFeatureNames());
		Assert.assertEquals(aggregatedFeatureEventFunction, actual.getAggregatedFeatureEventFunction());
	}

	@Test
	public void configuration_should_be_deserialized_from_event_json_with_empty_aggregated_feature_event_function() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				new JSONObject());
		AggregatedFeatureEventConf actual = (new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);

		Assert.assertNotNull(actual);
		Assert.assertEquals(NAME, actual.getName());
		Assert.assertEquals(BUCKET_CONF_NAME, actual.getBucketConfName());
		Assert.assertNull(actual.getBucketConf());
		Assert.assertEquals(NUMBER_OF_BUCKETS, actual.getNumberOfBuckets());
		Assert.assertEquals(BUCKETS_LEAP, actual.getBucketsLeap());
		Assert.assertEquals(WAIT_AFTER_BUCKET_CLOSE_SECONDS, actual.getWaitAfterBucketCloseSeconds());
		Assert.assertEquals(getSimpleAggregatedFeatureNamesMap(), actual.getAggregatedFeatureNamesMap());
		Assert.assertEquals(new JSONObject(), actual.getAggregatedFeatureEventFunction());

		Set<String> expectedAllAggregatedFeatureNames = new HashSet<>();
		expectedAllAggregatedFeatureNames.add("aggregatedFeatureName");
		Assert.assertEquals(expectedAllAggregatedFeatureNames, actual.getAllAggregatedFeatureNames());
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_name() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				null,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_bucket_conf_name() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				"",
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_number_of_buckets() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				0,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_buckets_leap() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				0,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_illegal_wait_after_bucket_close_seconds() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				-1,
				getSimpleAggregatedFeatureNamesMap(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_null_aggregated_feature_names_map() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				null,
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_empty_aggregated_feature_names_map() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				new HashMap<String, List<String>>(),
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_aggregated_feature_names_map_that_has_an_empty_list() throws Exception {
		Map<String, List<String>> aggregatedFeatureNamesMap = new HashMap<>();
		aggregatedFeatureNamesMap.put("functionArgument", new ArrayList<String>());
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				aggregatedFeatureNamesMap,
				getSimpleAggregatedFeatureEventFunction());
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_from_json_containing_event_with_null_aggregated_feature_event_function() throws Exception {
		String jsonAsString = createAggregatedFeatureEvent(
				NAME,
				TYPE,
				BUCKET_CONF_NAME,
				NUMBER_OF_BUCKETS,
				BUCKETS_LEAP,
				WAIT_AFTER_BUCKET_CLOSE_SECONDS,
				getSimpleAggregatedFeatureNamesMap(),
				null);
		(new ObjectMapper()).readValue(jsonAsString, AggregatedFeatureEventConf.class);
	}

	private static JSONObject createAggregatedFeatureEventFunction(String type, Map<String, String> params) {
		JSONObject result = new JSONObject();
		result.put("type", type);
		result.put("params", params);
		return result;
	}

	private static String createAggregatedFeatureEvent(
			String name,
			String type,
			String bucketConfName,
			int numberOfBuckets,
			int bucketsLeap,
			long waitAfterBucketCloseSeconds,
			Map<String, List<String>> aggregatedFeatureNamesMap,
			JSONObject aggregatedFeatureEventFunction) {

		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("type", type);
		result.put("bucketConfName", bucketConfName);
		result.put("numberOfBuckets", numberOfBuckets);
		result.put("bucketsLeap", bucketsLeap);
		result.put("waitAfterBucketCloseSeconds", waitAfterBucketCloseSeconds);
		result.put("aggregatedFeatureNamesMap", aggregatedFeatureNamesMap);
		result.put("aggregatedFeatureEventFunction", aggregatedFeatureEventFunction);
		return result.toJSONString();
	}

	private Map<String, List<String>> getSimpleAggregatedFeatureNamesMap() {
		List<String> list = new ArrayList<>();
		list.add("aggregatedFeatureName");
		Map<String, List<String>> map = new HashMap<>();
		map.put("functionArgument", list);
		return map;
	}

	private JSONObject getSimpleAggregatedFeatureEventFunction() {
		Map<String, String> map = new HashMap<>();
		map.put("functionParam", "valueOfFunctionParam");
		return createAggregatedFeatureEventFunction(FUNCTION_TYPE, map);
	}
}
