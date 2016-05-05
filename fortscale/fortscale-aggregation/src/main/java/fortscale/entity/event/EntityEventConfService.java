package fortscale.entity.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import groovy.json.JsonException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEventConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(EntityEventConfService.class);
	private static final String ENTITY_EVENT_DEFINITIONS_JSON_FIELD_NAME = "EntityEventDefinitions";

	@Value("${fortscale.entity.event.definitions.json.file.path}")
	private String entityEventDefinitionsConfJsonFilePath;
	@Value("${fortscale.entity.event.definitions.conf.json.overriding.files.path}")
	private String entityEventDefinitionsConfJsonOverridingFilesPath;

	@Autowired
	private EntityEventGlobalParamsConfService entityEventGlobalParamsConfService;

	private Map<String, EntityEventConf> entityEventDefinitions = new HashMap<>();
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected String getBaseConfJsonFilesPath() {
		return entityEventDefinitionsConfJsonFilePath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return entityEventDefinitionsConfJsonOverridingFilesPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return null;
	}

	@Override
	protected String getConfNodeName() {
		return ENTITY_EVENT_DEFINITIONS_JSON_FIELD_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObj) {
		String definitionAsString = jsonObj.toJSONString();

		try {
			EntityEventConf entityEventConf = objectMapper.readValue(definitionAsString, EntityEventConf.class);
			this.entityEventDefinitions.put(entityEventConf.getName(), entityEventConf);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize JSON %s", definitionAsString);
			logger.error(errorMsg, e);
			throw new JsonException(errorMsg, e);
		}
	}

	public List<EntityEventConf> getEntityEventDefinitions() {
		List<EntityEventConf> list = new ArrayList<>();
		entityEventDefinitions.values().forEach(list::add);
		return list;
	}

	public EntityEventConf getEntityEventConf(String name) {
		return entityEventDefinitions.get(name);
	}

	public Map<String, Object> getGlobalParams() {
		return entityEventGlobalParamsConfService.getGlobalParams();
	}

	public List<String> getEntityEventNames() {
		List<String> names = new ArrayList<>();

		entityEventDefinitions.values().forEach(entityEventConf -> {
			names.add(entityEventConf.getName());
		});

		return names;
	}
}
