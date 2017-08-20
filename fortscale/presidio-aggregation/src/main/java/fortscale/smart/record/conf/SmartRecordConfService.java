package fortscale.smart.record.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A service that manages all the {@link SmartRecordConf}s.
 *
 * @author Lior Govrin
 */
public class SmartRecordConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(SmartRecordConfService.class);
	private static final String SMART_RECORD_CONFS_NODE_NAME = "SmartRecordConfs";

	private String baseConfigurationsPath;
	private String overridingConfigurationsPath;
	private String additionalConfigurationsPath;
	private ObjectMapper objectMapper;
	private Map<String, SmartRecordConf> nameToSmartRecordConfMap;

	public SmartRecordConfService(
			String baseConfigurationsPath,
			String overridingConfigurationsPath,
			String additionalConfigurationsPath) {

		this.baseConfigurationsPath = baseConfigurationsPath;
		this.overridingConfigurationsPath = overridingConfigurationsPath;
		this.additionalConfigurationsPath = additionalConfigurationsPath;
		this.objectMapper = new ObjectMapper();
		this.nameToSmartRecordConfMap = new HashMap<>();
	}

	@Override
	protected String getBaseConfJsonFilesPath() {
		return baseConfigurationsPath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return overridingConfigurationsPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return additionalConfigurationsPath;
	}

	@Override
	protected String getConfNodeName() {
		return SMART_RECORD_CONFS_NODE_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObject) {
		String jsonString = jsonObject.toJSONString();
		SmartRecordConf smartRecordConf;

		try {
			smartRecordConf = objectMapper.readValue(jsonString, SmartRecordConf.class);
		} catch (Exception e) {
			String msg = String.format("Failed to deserialize JSON string %s.", jsonString);
			logger.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}

		// TODO: If defined, add to smart record conf singleton cluster confs for missing aggregation record names
		nameToSmartRecordConfMap.put(smartRecordConf.getName(), smartRecordConf);
	}

	public SmartRecordConf getSmartRecordConf(String name) {
		return nameToSmartRecordConfMap.get(name);
	}
}
