package fortscale.ml.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.*;

public class ModelConfService implements ApplicationContextAware, InitializingBean {
	private static final Logger logger = Logger.getLogger(ModelConfService.class);
	private static final String MODEL_CONFS_JSON_FIELD_NAME = "ModelConfs";

	@Value("${fortscale.model.configurations.location.path}")
	private String modelConfigurationsLocationPath;

	private ApplicationContext applicationContext;
	private List<ModelConf> modelConfs;
	private Map<String, ModelConf> nameToModelConfMap;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		modelConfs = new ArrayList<>();
		nameToModelConfMap = new HashMap<>();
		loadModelConfs();
	}

	public List<ModelConf> getModelConfs() {
		return modelConfs;
	}

	@SuppressWarnings("unused")
	public ModelConf getModelConf(String modelConfName) {
		return nameToModelConfMap.get(modelConfName);
	}

	private void loadModelConfs() {
		String errorMsg;
		List<Object> modelConfJSONs = getModelConfsFromAllResources();
		ObjectMapper objectMapper = new ObjectMapper();

		for (Object modelConfJSON : modelConfJSONs) {
			String jsonString = ((JSONObject)modelConfJSON).toJSONString();

			try {
				ModelConf modelConf = objectMapper.readValue(jsonString, ModelConf.class);
				String modelConfName = modelConf.getName();

				if (nameToModelConfMap.containsKey(modelConfName)) {
					errorMsg = String.format(
							"Model configuration names must be unique. %s appears multiple times.",
							modelConfName);
					logger.error(errorMsg);
					throw new IllegalArgumentException(errorMsg);
				}

				modelConfs.add(modelConf);
				nameToModelConfMap.put(modelConfName, modelConf);
			} catch (Exception e) {
				errorMsg = String.format("Failed to deserialize model conf JSON %s.", jsonString);
				logger.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg, e);
			}
		}
	}

	private List<Object> getModelConfsFromAllResources() {
		String errorMsg;
		List<Resource> resources;
		List<Object> modelConfs = new ArrayList<>();

		try {
			resources = Arrays.asList(applicationContext
					.getResources(modelConfigurationsLocationPath.concat("/*.json")));
		} catch (Exception e) {
			errorMsg = String.format(
					"Failed to get model confs resources from location path %s.",
					modelConfigurationsLocationPath);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}

		for (Resource resource : resources) {
			modelConfs.addAll(getModelConfsFromResource(resource));
		}

		return modelConfs;
	}

	private static JSONArray getModelConfsFromResource(Resource resource) {
		String errorMsg;
		JSONArray modelConfs;

		try {
			JSONObject json = (JSONObject)JSONValue.parseWithException(resource.getInputStream());
			modelConfs = (JSONArray)json.get(MODEL_CONFS_JSON_FIELD_NAME);
		} catch (Exception e) {
			errorMsg = String.format(
					"Failed to parse model confs JSON file %s.",
					resource.getFilename());
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}

		if (modelConfs == null) {
			errorMsg = String.format(
					"Model confs JSON file %s does not contain field %s.",
					resource.getFilename(), MODEL_CONFS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		return modelConfs;
	}
}
