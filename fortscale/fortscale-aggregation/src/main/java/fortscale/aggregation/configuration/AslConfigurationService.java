package fortscale.aggregation.configuration;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class AslConfigurationService implements InitializingBean, ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(AslConfigurationService.class);

	private ApplicationContext applicationContext;

	protected abstract String getBaseConfJsonFilesPath();

	protected abstract String getBaseOverridingConfJsonFolderPath();

	protected abstract String getAdditionalConfJsonFolderPath();

	protected abstract String getConfNodeName();

	protected abstract void loadConfJson(JSONObject confJsonObject);

	@Override
	public void afterPropertiesSet() throws Exception {
		loadConfs();
	}

	protected void loadConfs() throws IllegalArgumentException {
		loadBaseConfs();
		loadAdditionalConfs();
	}

	private void loadBaseConfs() throws IllegalArgumentException {
		Resource[] confJsonResources = null;
		//load overriding base configuration if exist
		if (getBaseOverridingConfJsonFolderPath() != null) {
			try {
				confJsonResources = applicationContext.getResources(getBaseOverridingConfJsonFolderPath());
			} catch (Exception e) {
			}
		}

		//if base overriding configuration does not exist
		if (confJsonResources == null || confJsonResources.length == 0) {
			try {
				confJsonResources = applicationContext.getResources(getBaseConfJsonFilesPath());
			} catch (Exception e) {
				String errorMsg = String.format("Failed to open json file %s", getBaseConfJsonFilesPath());
				logger.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg, e);
			}

			if (confJsonResources == null || confJsonResources.length == 0) {
				String errorMsg = String.format("json file %s doesn't exist.", getBaseConfJsonFilesPath());
				logger.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
		}

		loadConfResources(confJsonResources);
	}

	private void loadAdditionalConfs() throws IllegalArgumentException {
		if (getAdditionalConfJsonFolderPath() == null) {
			return;
		}

		Resource[] confJsonResources = null;
		try {
			confJsonResources = applicationContext.getResources(getAdditionalConfJsonFolderPath());
		} catch (Exception e) {
		}

		if (confJsonResources != null && confJsonResources.length > 0) {
			loadConfResources(confJsonResources);
		}
	}

	private void loadConfResources(Resource[] confJsonResources) throws IllegalArgumentException {
		Map<String, InputStream> fileNameToInputStream = new HashMap<>();
		for (Resource confJsonResource : confJsonResources) {
			try {
				InputStream inputStream = confJsonResource.getInputStream();
				fileNameToInputStream.put(confJsonResource.getFilename(), inputStream);
			} catch (Exception e) {
				String errorMsg = String.format("Failed to open json file %s", confJsonResource.getFilename());
				logger.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg, e);
			}
		}

		for (Map.Entry<String, InputStream> entry : fileNameToInputStream.entrySet()) {
			try {
				loadConfInputStream(entry.getValue());
			} catch (Exception e) {
				String errorMsg = String.format("Failed to load Configuration from json file %s", entry.getKey());
				logger.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg, e);
			}
		}
	}

	private void loadConfInputStream(InputStream inputStream) throws IOException, ParseException {
		JSONObject jsonObj = (JSONObject)JSONValue.parseWithException(inputStream);

		if (jsonObj.get(getConfNodeName()) instanceof JSONArray) {
			JSONArray confsJson = (JSONArray)jsonObj.get(getConfNodeName());

			for (Object obj : confsJson) {
				loadConfJson((JSONObject)obj);
			}
		} else {
			loadConfJson((JSONObject)jsonObj.get(getConfNodeName()));
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
