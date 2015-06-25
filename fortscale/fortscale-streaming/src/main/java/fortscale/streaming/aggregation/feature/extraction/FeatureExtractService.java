package fortscale.streaming.aggregation.feature.extraction;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.utils.logging.Logger;


public class FeatureExtractService implements IFeatureExtractService {
	private static final Logger logger = Logger.getLogger(FeatureExtractService.class);

	public static final String FEATURE_CONF_FILE_JASON_PROPERTY_NAME = "fortscale.aggregation.feature.extraction.feature-conf-jason";
	public static final String FEATURE_CONF_FILE_JASON_DEAFULT_VALUE = "feature-conf.jason";
	private static final String	JSON_CONF_FEATURE_CONFS_NODE_NAME = 	"FeatureConfs";
	private static final String JSON_CONF_EXTRACTOR_NODE_NAME = "extractor";

	private Map<String, FeatureExtractor> featureExtractorMap = new HashMap<>();
	private JSONObject featuresConfJson;

	public FeatureExtractService(Config config) {
		String featuresConfJasonFileName = FEATURE_CONF_FILE_JASON_DEAFULT_VALUE;

		if(config !=null) {
			featuresConfJasonFileName = config.get(FEATURE_CONF_FILE_JASON_PROPERTY_NAME, FEATURE_CONF_FILE_JASON_DEAFULT_VALUE);
		}

		setFeaturesConfJsonFromFile(featuresConfJasonFileName);

	}


	/**
	 *
	 * @param confJson must include a "FeatureConfs" node with feature definitions
	 */
	public FeatureExtractService(JSONObject confJson) {
		initFeatureConfJson(confJson);
	}

	/**
	 *
	 * @param confJson must include a "FeatureConfs" node with feature definitions
	 */
	private void initFeatureConfJson(@NotNull JSONObject confJson) {
		this.featuresConfJson = (JSONObject)confJson.get(JSON_CONF_FEATURE_CONFS_NODE_NAME);
	}
	private void setFeaturesConfJsonFromFile(String fileName) throws IllegalArgumentException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(fileName));
			initFeatureConfJson(jsonObj);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to read json conf file %s", fileName);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	private FeatureExtractor getFeatureExtractor(String featureName) {
		FeatureExtractor fe = featureExtractorMap.get(featureName);
		if(fe==null) {
			fe = createFeatureExtractor(featureName);
		}
		return fe;
	}

	private FeatureExtractor createFeatureExtractor(String featureName) {
		FeatureExtractor featureExtractor;
		JSONObject featureJson = (JSONObject)featuresConfJson.get(featureName);

		if(featureJson==null) {
			String errorMsg = String.format("No such feature name: %s", featureName);
			logger.error(errorMsg);
			return null;
		}

		JSONObject extractorJson = (JSONObject)featureJson.get(JSON_CONF_EXTRACTOR_NODE_NAME);
		if(extractorJson==null) {
			String errorMsg = String.format("No extractor node in feature: %s", featureName);
			logger.error(errorMsg);
			return null;
		}

		try {
			featureExtractor = (new ObjectMapper()).readValue(extractorJson.toJSONString(), FeatureExtractor.class);
			featureExtractorMap.put(featureName, featureExtractor);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize json %s", featureJson.toJSONString());
			logger.error(errorMsg, e);
			return null;
		}
		return featureExtractor;

	}

	@Override
	public Feature extract(String featureName, JSONObject eventMessage){
		FeatureExtractor featureExtractor = getFeatureExtractor(featureName);

		Object value;

		if(featureExtractor != null){
			value = featureExtractor.extract(eventMessage);
		} else {
			value = eventMessage.get(featureName);
		}
		return new Feature(featureName, value);

	}

	@Override
	public Map<String, Feature> extract(Set<String> featureNames, JSONObject message) {
		if(featureNames == null || message	== null) {
			return null;
		}
		Map<String, Feature> features = new HashMap<>();

		for (String featureName: featureNames) {
			FeatureExtractor fe = getFeatureExtractor(featureName);
			if(fe!=null) {
				Object value = fe.extract(message);
				features.put(featureName, new Feature(featureName, value));
			}
		}
		return features;
	}
}
