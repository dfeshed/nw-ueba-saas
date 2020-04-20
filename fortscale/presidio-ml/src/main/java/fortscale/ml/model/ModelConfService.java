package fortscale.ml.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationServiceBase;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelConfService extends AslConfigurationServiceBase {
	private static final Logger logger = Logger.getLogger(ModelConfService.class);
	private static final String MODEL_CONFS_JSON_FIELD_NAME = "ModelConfs";

	private Resource[] baseConfigurationResources;
	private Resource[] overridingConfigurationResources;
	private Resource[] additionalConfigurationResources;
	private List<ModelConf> modelConfs = new ArrayList<>();
	private Map<String, ModelConf> nameToModelConfMap = new HashMap<>();
	private ObjectMapper objectMapper;

	public ModelConfService(
			Resource[] baseConfigurationResources,
			Resource[] overridingConfigurationResources,
			Resource[] additionalConfigurationResources) {

		this.baseConfigurationResources = baseConfigurationResources;
		this.overridingConfigurationResources = overridingConfigurationResources;
		this.additionalConfigurationResources = additionalConfigurationResources;
		this.objectMapper=ObjectMapperProvider.getInstance().getDefaultObjectMapper();
	}

	@Override
	protected Resource[] getBaseConfigurationResources() throws IOException {
		return baseConfigurationResources;
	}

	@Override
	protected Resource[] getOverridingConfigurationResources() throws IOException {
		return overridingConfigurationResources;
	}

	@Override
	protected Resource[] getAdditionalConfigurationResources() throws IOException {
		return additionalConfigurationResources;
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
			modelConf = objectMapper.readValue(jsonString, ModelConf.class);
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

	public List<ModelConf> getModelConfsByBuilderConfType(Class<? extends IModelBuilderConf> type) {
		return modelConfs.stream().filter(x -> type.isInstance(x.getModelBuilderConf())).collect(Collectors.toList());
	}


	public ModelConf getModelConf(String modelConfName) {
		return nameToModelConfMap.get(modelConfName);
	}
}
