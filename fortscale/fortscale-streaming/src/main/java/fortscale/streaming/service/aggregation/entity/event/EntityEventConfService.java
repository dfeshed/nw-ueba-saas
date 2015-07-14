package fortscale.streaming.service.aggregation.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEventConfService implements InitializingBean {
	private static final Logger logger = Logger.getLogger(EntityEventConfService.class);

	private static final String ENTITY_EVENTS_JSON_FIELD_NAME = "EntityEvents";
	private static final String GLOBAL_PARAMS_JSON_FIELD_NAME = "GlobalParams";
	private static final String ENTITY_EVENT_DEFINITIONS_JSON_FIELD_NAME = "EntityEventDefinitions";

	@Value("${fortscale.aggregation.entity.event.definitions.json.file.path}")
	private String entityEventDefinitionsJsonFilePath;

	private Map<String, Object> globalParams;
	private List<EntityEventConf> entityEventDefinitions;

	@Override
	public void afterPropertiesSet() throws Exception {
		loadEntityEventsJsonFile();
	}

	public Map<String, Object> getGlobalParams() {
		return globalParams;
	}

	public List<EntityEventConf> getEntityEventDefinitions() {
		return entityEventDefinitions;
	}

	private void loadEntityEventsJsonFile() {
		JSONObject entityEvents;
		String errorMsg;

		try {
			entityEvents = (JSONObject)JSONValue.parseWithException(new FileReader(entityEventDefinitionsJsonFilePath));
			entityEvents = (JSONObject)entityEvents.get(ENTITY_EVENTS_JSON_FIELD_NAME);
		} catch (Exception e) {
			errorMsg = String.format("Failed to parse JSON file %s", entityEventDefinitionsJsonFilePath);
			logger.error(errorMsg, e);
			throw new JsonException(errorMsg, e);
		}

		if (entityEvents == null) {
			errorMsg = String.format("JSON file %s does not contain field %s", entityEventDefinitionsJsonFilePath, ENTITY_EVENTS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new JsonException(errorMsg);
		}

		loadGlobalParams(entityEvents);
		loadEntityEventDefinitions(entityEvents);
	}

	private void loadGlobalParams(JSONObject entityEvents) {
		JSONObject globalParams = (JSONObject)entityEvents.get(GLOBAL_PARAMS_JSON_FIELD_NAME);
		if (globalParams == null) {
			String errorMsg = String.format("JSON file %s does not contain field %s", entityEventDefinitionsJsonFilePath, GLOBAL_PARAMS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new JsonException(errorMsg);
		}

		this.globalParams = new HashMap<>();
		for (Map.Entry<String, Object> entry : globalParams.entrySet()) {
			this.globalParams.put(entry.getKey(), entry.getValue());
		}
	}

	private void loadEntityEventDefinitions(JSONObject entityEvents) {
		JSONArray entityEventDefinitions = (JSONArray)entityEvents.get(ENTITY_EVENT_DEFINITIONS_JSON_FIELD_NAME);
		String errorMsg;
		if (entityEventDefinitions == null) {
			errorMsg = String.format("JSON file %s does not contain array %s", entityEventDefinitionsJsonFilePath, ENTITY_EVENT_DEFINITIONS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new JsonException(errorMsg);
		}

		ObjectMapper objectMapper = new ObjectMapper();
		this.entityEventDefinitions = new ArrayList<>();
		for (Object definition : entityEventDefinitions) {
			String definitionAsString = ((JSONObject)definition).toJSONString();
			try {
				EntityEventConf entityEventConf = objectMapper.readValue(definitionAsString, EntityEventConf.class);
				this.entityEventDefinitions.add(entityEventConf);
			} catch (Exception e) {
				errorMsg = String.format("Failed to deserialize JSON %s", definitionAsString);
				logger.error(errorMsg, e);
				throw new JsonException(errorMsg, e);
			}
		}
	}
}
