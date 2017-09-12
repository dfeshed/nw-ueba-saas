package fortscale.smart.record.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Lior Govrin
 */
public class SmartRecordConfTest {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static String DEFAULT_SMART_RECORD_CONF_STRING = getSmartRecordConfJson(
			"testSmartRecord", getJsonArray("userId"), "fixed_duration_hourly", false, 0.25,
			getJsonArray(getClusterConfJson(getJsonArray("testAggregationRecord"), 0.75)))
			.toString();

	@Test
	public void should_fail_if_name_field_is_invalid() {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		int numberOfExpectedExceptions = 0;
		jsonObject.put("name", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("name", "");
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("name", "   ");
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		Assert.assertEquals(3, numberOfExpectedExceptions);
	}

	@Test
	public void should_fail_if_contexts_field_is_invalid() {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		int numberOfExpectedExceptions = 0;
		jsonObject.put("contexts", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("contexts", getJsonArray());
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("contexts", getJsonArray(JSONObject.NULL));
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("contexts", getJsonArray(""));
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("contexts", getJsonArray("   "));
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		Assert.assertEquals(5, numberOfExpectedExceptions);
	}

	@Test
	public void should_fail_if_fixed_duration_strategy_field_is_invalid() {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		int numberOfExpectedExceptions = 0;
		jsonObject.put("fixedDurationStrategy", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("fixedDurationStrategy", "");
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("fixedDurationStrategy", "   ");
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("fixedDurationStrategy", "A");
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		Assert.assertEquals(4, numberOfExpectedExceptions);
	}

	@Test
	public void should_succeed_if_default_weight_field_is_valid() throws Exception {
		// Not all aggregation records should be included, and all the
		// cluster confs have a weight, so a default weight is optional
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("defaultWeight", JSONObject.NULL);
		SmartRecordConf smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(null, smartRecordConf.getDefaultWeight());

		// All aggregation records should be included, and not all the
		// cluster confs have a weight, so a default weight is mandatory
		jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("includeAllAggregationRecords", true);
		jsonObject.getJSONArray("clusterConfs").getJSONObject(0).put("weight", JSONObject.NULL);
		smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(0.25, smartRecordConf.getDefaultWeight(), 0);
	}

	@Test
	public void should_fail_if_default_weight_field_is_invalid() {
		int numberOfExpectedExceptions = 0;

		// All aggregation records should be included, but there isn't a default weight
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("includeAllAggregationRecords", true);
		jsonObject.put("defaultWeight", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		// The weight is missing in one of the cluster confs, and there isn't a default weight
		jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("defaultWeight", JSONObject.NULL);
		jsonObject.getJSONArray("clusterConfs").getJSONObject(0).put("weight", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		// The default weight is smaller than 0
		jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("includeAllAggregationRecords", true);
		jsonObject.put("defaultWeight", -100);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		// The default weight is greater than 1
		jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.put("defaultWeight", 100);
		jsonObject.getJSONArray("clusterConfs").getJSONObject(0).put("weight", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		Assert.assertEquals(4, numberOfExpectedExceptions);
	}

	@Test
	public void should_succeed_if_cluster_confs_field_is_valid() throws Exception {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);

		// Not all aggregation records should be included, and there is at least one cluster conf
		SmartRecordConf smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(1, smartRecordConf.getClusterConfs().size());

		// All aggregation records should be included, so the cluster confs list can be null
		jsonObject.put("includeAllAggregationRecords", true);
		jsonObject.put("clusterConfs", JSONObject.NULL);
		smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(0, smartRecordConf.getClusterConfs().size());

		// All aggregation records should be included, so the cluster confs list can be empty
		jsonObject.put("clusterConfs", getJsonArray());
		smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(0, smartRecordConf.getClusterConfs().size());
	}

	@Test
	public void should_fail_if_cluster_confs_field_is_invalid() {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		int numberOfExpectedExceptions = 0;

		// Not all aggregation records should be included, but the cluster confs list is null
		jsonObject.put("clusterConfs", JSONObject.NULL);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		// Not all aggregation records should be included, but the cluster confs list is empty
		jsonObject.put("clusterConfs", getJsonArray());
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		// The cluster confs list cannot contain nulls, whether all aggregation records should be included or not
		jsonObject.put("clusterConfs", getJsonArray(JSONObject.NULL));
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;
		jsonObject.put("includeAllAggregationRecords", true);
		if (doesDeserializationThrowException(jsonObject)) numberOfExpectedExceptions++;

		Assert.assertEquals(4, numberOfExpectedExceptions);
	}

	@Test
	public void should_return_the_correct_set_of_aggregation_record_names() throws Exception {
		HashSet<String> expected = new HashSet<>(Arrays.asList("testAggregationRecord", "myAggregationRecord"));
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.getJSONArray("clusterConfs").put(getClusterConfJson(getJsonArray("myAggregationRecord"), 0.5));
		SmartRecordConf smartRecordConf = objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
		Assert.assertEquals(expected, smartRecordConf.getAggregationRecordNames());
	}

	@Test
	public void should_fail_if_an_aggregation_record_name_is_defined_multiple_times() {
		JSONObject jsonObject = new JSONObject(DEFAULT_SMART_RECORD_CONF_STRING);
		jsonObject.getJSONArray("clusterConfs").put(getClusterConfJson(getJsonArray("testAggregationRecord"), 0.5));
		Assert.assertTrue(doesDeserializationThrowException(jsonObject));
	}

	@Test
	public void should_be_able_to_add_new_cluster_confs() throws Exception {
		SmartRecordConf smartRecordConf = objectMapper.readValue(DEFAULT_SMART_RECORD_CONF_STRING, SmartRecordConf.class);
		List<ClusterConf> clusterConfs = smartRecordConf.getClusterConfs();
		clusterConfs.add(new ClusterConf(Collections.singletonList("myAggregationRecord"), 0.5));
		Assert.assertEquals(2, clusterConfs.size());
	}

	private static JSONArray getJsonArray(Object... values) {
		JSONArray jsonArray = new JSONArray();
		for (Object value : values) jsonArray.put(value);
		return jsonArray;
	}

	private static JSONObject getClusterConfJson(JSONArray aggregationRecordNames, Double weight) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aggregationRecordNames", aggregationRecordNames);
		jsonObject.put("weight", weight);
		return jsonObject;
	}

	private static JSONObject getSmartRecordConfJson(
			String name,
			JSONArray contexts,
			String fixedDurationStrategy,
			boolean includeAllAggregationRecords,
			Double defaultWeight,
			JSONArray clusterConfs) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", name);
		jsonObject.put("contexts", contexts);
		jsonObject.put("fixedDurationStrategy", fixedDurationStrategy);
		jsonObject.put("includeAllAggregationRecords", includeAllAggregationRecords);
		jsonObject.put("defaultWeight", defaultWeight);
		jsonObject.put("clusterConfs", clusterConfs);
		return jsonObject;
	}

	private static boolean doesDeserializationThrowException(JSONObject jsonObject) {
		try {
			objectMapper.readValue(jsonObject.toString(), SmartRecordConf.class);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}
