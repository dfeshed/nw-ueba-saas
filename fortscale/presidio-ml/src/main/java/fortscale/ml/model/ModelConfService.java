package fortscale.ml.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(ModelConfService.class);
	private static final String MODEL_CONFS_JSON_FIELD_NAME = "ModelConfs";

	private String baseConfigurationPath;
	private String overridingConfigurationPath;
	private String additionalConfigurationPath;

	public ModelConfService(AslConfigurationPaths modelConfigurationPaths) {
		this.baseConfigurationPath = modelConfigurationPaths.getBaseConfigurationPath();
		this.overridingConfigurationPath = modelConfigurationPaths.getOverridingConfigurationPath();
		this.additionalConfigurationPath = modelConfigurationPaths.getAdditionalConfigurationPath();
	}

	private List<ModelConf> modelConfs = new ArrayList<>();
	private Map<String, ModelConf> nameToModelConfMap = new HashMap<>();

	@Override
	protected String getBaseConfJsonFilesPath() {
		return baseConfigurationPath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return overridingConfigurationPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return additionalConfigurationPath;
	}

	@Override
	protected String getConfNodeName() {
		return MODEL_CONFS_JSON_FIELD_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObject) {
		String errorMessage;

		if (jsonObject == null) {
			errorMessage = "Received a null model configuration JSON object.";
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		String jsonString = jsonObject.toJSONString();
		ModelConf modelConf;

		try {
			modelConf = new ObjectMapper().readValue(jsonString, ModelConf.class);
			Assert.notNull(modelConf, "Model configuration cannot be null.");
		} catch (Exception e) {
			errorMessage = String.format("Failed to deserialize model configuration JSON string %s.", jsonString);
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		}

		String modelConfName = modelConf.getName();

		if (nameToModelConfMap.containsKey(modelConfName)) {
			errorMessage = String.format("Model configuration names must be unique. %s appears multiple times.", modelConfName);
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		modelConfs.add(modelConf);
		nameToModelConfMap.put(modelConfName, modelConf);
	}

	public List<ModelConf> getModelConfs() {
		return modelConfs;
	}

	public ModelConf getModelConf(String modelConfName) {
		return nameToModelConfMap.get(modelConfName);
	}
}
