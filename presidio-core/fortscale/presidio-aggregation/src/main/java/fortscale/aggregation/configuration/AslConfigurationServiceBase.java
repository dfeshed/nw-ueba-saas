package fortscale.aggregation.configuration;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract service that loads JSON configurations. The inheritors need to:
 * 1. Supply the resources for the base, overriding and additional configurations.
 * 2. Supply the name of the node within the JSON that contains the configuration(s).
 * 3. Implement the method that parses a single JSON object to the desired POJO.
 * <p>
 * First, the service checks for any overriding configurations and loads them if they exist. If there aren't
 * any overriding configurations, the service loads the base configurations. If both the overriding and the base
 * configurations are not found, the service fails. Afterwards, the service loads any additional configurations.
 *
 * @author Yaron DL
 * @author Lior Govrin
 */
public abstract class AslConfigurationServiceBase {
	private static final Logger logger = Logger.getLogger(AslConfigurationServiceBase.class);

	/**
	 * @return the resources for the base configurations
	 */
	protected abstract Resource[] getBaseConfigurationResources() throws IOException;

	/**
	 * @return the resources for the overriding configurations
	 */
	protected abstract Resource[] getOverridingConfigurationResources() throws IOException;

	/**
	 * @return the resources for the additional configurations
	 */
	protected abstract Resource[] getAdditionalConfigurationResources() throws IOException;

	/**
	 * @return the name of the node within the JSON that contains the configuration(s)
	 */
	protected abstract String getConfNodeName();

	/**
	 * Parse a single JSON object to the desired POJO.
	 *
	 * @param confJsonObject the JSON object to parse
	 */
	protected abstract void loadConfJson(JSONObject confJsonObject);

	/**
	 * Load the ASL configurations from the resources, as described in the class documentation.
	 */
	public void loadAslConfigurations() throws IllegalArgumentException {
		loadOverridingOrBaseConfigurations();
		loadAdditionalConfigurations();
	}

	private void loadOverridingOrBaseConfigurations() throws IllegalArgumentException {
		Resource[] resources;

		try {
			resources = getOverridingConfigurationResources();
		} catch (IOException e) {
			logger.info("Overriding resources missing or cannot be accessed - Getting base resources.", e);
			resources = null;
		}

		if (resources == null || resources.length == 0) {
			try {
				resources = getBaseConfigurationResources();
			} catch (IOException e) {
				String msg = "Base resources missing or cannot be accessed.";
				logger.error(msg, e);
				throw new IllegalArgumentException(msg, e);
			}

			if (resources == null || resources.length == 0) {
				String msg = "There are no base resources.";
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
		}

		loadConfigurationResources(resources);
	}

	private void loadAdditionalConfigurations() throws IllegalArgumentException {
		Resource[] resources;

		try {
			resources = getAdditionalConfigurationResources();
		} catch (IOException e) {
			logger.info("Additional resources missing or cannot be accessed.", e);
			resources = null;
		}

		if (resources != null && resources.length > 0) {
			loadConfigurationResources(resources);
		}
	}

	private void loadConfigurationResources(Resource[] resources) throws IllegalArgumentException {
		Map<String, InputStream> filePathToInputStreamMap = new HashMap<>();

		for (Resource resource : resources) {
			try {
				InputStream inputStream = resource.getInputStream();
				String filePath = resource.getURI().toString();
				filePathToInputStreamMap.put(filePath, inputStream);
			} catch (Exception e) {
				String msg = String.format("Failed to open JSON file %s.", resource.getFilename());
				logger.error(msg, e);
				throw new IllegalArgumentException(msg, e);
			}
		}

		for (Map.Entry<String, InputStream> entry : filePathToInputStreamMap.entrySet()) {
			try {
				loadInputStreamConfigurations(entry.getValue());
			} catch (Exception e) {
				String msg = String.format("Failed to load configurations from JSON file %s.", entry.getKey());
				logger.error(msg, e);
				throw new IllegalArgumentException(msg, e);
			}
		}
	}

	private void loadInputStreamConfigurations(InputStream inputStream) throws IOException, ParseException {
		JSONObject jsonObject = (JSONObject)JSONValue.parseWithException(inputStream);
		inputStream.close();

		if (jsonObject.get(getConfNodeName()) instanceof JSONArray) {
			// An array of JSON objects, each one is parsed to the desired POJO
			JSONArray jsonArray = (JSONArray)jsonObject.get(getConfNodeName());

			for (Object object : jsonArray) {
				loadConfJson((JSONObject)object);
			}
		} else {
			// One JSON object, that is parsed to the desired POJO
			loadConfJson((JSONObject)jsonObject.get(getConfNodeName()));
		}
	}
}
