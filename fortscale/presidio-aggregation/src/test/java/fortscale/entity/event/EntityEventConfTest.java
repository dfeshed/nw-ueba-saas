package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEventConfTest {
	private static final String DEFAULT_NAME = "testEntityEventDefinition";
	private static final String DEFAULT_CONTEXT_FIELD = "normalized_username";
	private static final String DEFAULT_FUNCTION_ARGUMENT = "testFunctionArgument";
	private static final String DEFAULT_AGGREGATED_FEATURE_EVENT = "testAggregatedFeatureEvent";
	private static final String DEFAULT_TYPE = "testType";
	private static final String DEFAULT_FUNCTION_PARAM = "testFunctionParam";
	private static final String DEFAULT_FUNCTION_PARAM_VALUE = "testFunctionParamValue";

	private JSONObject getEntityEventFunction(String type, Map<String, String> params) {
		JSONObject entityEventFunction = new JSONObject();
		entityEventFunction.put("type", type);
		entityEventFunction.put("params", params);
		return entityEventFunction;
	}

	private JSONObject getEntityEventDef(String name, List<String> contextFields, Map<String, List<String>> aggregatedFeatureEventNamesMap, JSONObject entityEventFunction) {
		JSONObject entityEventDef = new JSONObject();
		entityEventDef.put("name", name);
		entityEventDef.put("contextFields", contextFields);
		entityEventDef.put("aggregatedFeatureEventNamesMap", aggregatedFeatureEventNamesMap);
		entityEventDef.put("entityEventFunction", entityEventFunction);
		return entityEventDef;
	}

	private List<String> getDefaultContextFields() {
		List<String> defaultContextFields = new ArrayList<>();
		defaultContextFields.add(DEFAULT_CONTEXT_FIELD);
		return defaultContextFields;
	}

	private Map<String, List<String>> getDefaultAggregatedFeatureEventNamesMap() {
		List<String> defaultAggregatedFeatureEvents = new ArrayList<>();
		defaultAggregatedFeatureEvents.add(DEFAULT_AGGREGATED_FEATURE_EVENT);
		Map<String, List<String>> defaultAggregatedFeatureEventNamesMap = new HashMap<>();
		defaultAggregatedFeatureEventNamesMap.put(DEFAULT_FUNCTION_ARGUMENT, defaultAggregatedFeatureEvents);
		return defaultAggregatedFeatureEventNamesMap;
	}

	private JSONObject getDefaultEntityEventFunction() {
		Map<String, String> defaultParams = new HashMap<>();
		defaultParams.put(DEFAULT_FUNCTION_PARAM, DEFAULT_FUNCTION_PARAM_VALUE);
		return getEntityEventFunction(DEFAULT_TYPE, defaultParams);
	}

	private JSONObject getDefaultEntityEventDef() {
		return getEntityEventDef(DEFAULT_NAME, getDefaultContextFields(), getDefaultAggregatedFeatureEventNamesMap(), getDefaultEntityEventFunction());
	}

	@Test
	public void entity_event_conf_should_be_deserialized_from_json() throws Exception {
		String jsonAsString = getDefaultEntityEventDef().toJSONString();
		EntityEventConf actual = (new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
		Assert.assertNotNull(actual);

		// Check name
		Assert.assertEquals(DEFAULT_NAME, actual.getName());

		// Check context fields
		List<String> actualContextFields = actual.getContextFields();
		Assert.assertNotNull(actualContextFields);
		Assert.assertEquals(1, actualContextFields.size());
		Assert.assertEquals(DEFAULT_CONTEXT_FIELD, actualContextFields.get(0));

		// Check aggregated feature event names map
		Map<String, List<String>> actualAggregatedFeatureEventNamesMap = actual.getAggregatedFeatureEventNamesMap();
		Assert.assertNotNull(actualAggregatedFeatureEventNamesMap);
		Assert.assertEquals(1, actualAggregatedFeatureEventNamesMap.size());
		List<String> actualAggregatedFeatureEvents = actualAggregatedFeatureEventNamesMap.get(DEFAULT_FUNCTION_ARGUMENT);
		Assert.assertNotNull(actualAggregatedFeatureEvents);
		Assert.assertEquals(1, actualAggregatedFeatureEvents.size());
		Assert.assertEquals(DEFAULT_AGGREGATED_FEATURE_EVENT, actualAggregatedFeatureEvents.get(0));

		// Check entity event function
		JSONObject expectedEntityEventFunction = getDefaultEntityEventFunction();
		JSONObject actualEntityEventFunction = actual.getEntityEventFunction();
		Assert.assertNotNull(actualEntityEventFunction);
		Assert.assertEquals(expectedEntityEventFunction, actualEntityEventFunction);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_null_name() throws Exception {
		String jsonAsString = getEntityEventDef(null, getDefaultContextFields(), getDefaultAggregatedFeatureEventNamesMap(), getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_blank_name() throws Exception {
		String jsonAsString = getEntityEventDef("   ", getDefaultContextFields(), getDefaultAggregatedFeatureEventNamesMap(), getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_null_context_fields() throws Exception {
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, null, getDefaultAggregatedFeatureEventNamesMap(), getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_a_blank_context_field() throws Exception {
		List<String> contextFields = new ArrayList<>();
		contextFields.add("   ");
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, contextFields, getDefaultAggregatedFeatureEventNamesMap(), getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_null_aggregated_feature_event_names_map() throws Exception {
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, getDefaultContextFields(), null, getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_a_blank_aggregated_feature_event_name() throws Exception {
		List<String> aggregatedFeatureEventNames = new ArrayList<>();
		aggregatedFeatureEventNames.add("validAggregatedFeatureEventName");
		aggregatedFeatureEventNames.add("");
		Map<String, List<String>> aggregatedFeatureEventNamesMap = new HashMap<>();
		aggregatedFeatureEventNamesMap.put(DEFAULT_FUNCTION_ARGUMENT, aggregatedFeatureEventNames);
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, getDefaultContextFields(), aggregatedFeatureEventNamesMap, getDefaultEntityEventFunction()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_null_entity_event_function() throws Exception {
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, getDefaultContextFields(), getDefaultAggregatedFeatureEventNamesMap(), null).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}

	@Test(expected = Exception.class)
	public void should_fail_when_trying_to_deserialize_entity_event_conf_json_with_empty_entity_event_function() throws Exception {
		String jsonAsString = getEntityEventDef(DEFAULT_NAME, getDefaultContextFields(), getDefaultAggregatedFeatureEventNamesMap(), new JSONObject()).toJSONString();
		(new ObjectMapper()).readValue(jsonAsString, EntityEventConf.class);
	}
}
