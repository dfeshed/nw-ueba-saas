package fortscale.streaming.aggregation.feature.extraction;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.utils.logging.Logger;

@Service
public class FeatureExtractService implements IFeatureExtractService, InitializingBean {
	private static final Logger logger = Logger.getLogger(FeatureExtractService.class);

	private static final String JSON_CONF_FEATURE_CONFS_NODE_NAME = "FeatureConfs";
	private static final String JSON_CONF_EXTRACTOR_NODE_NAME = "extractor";

	private Map<String, FeatureExtractor> featureExtractorMap = new HashMap<>();
	private JSONObject featuresConfJson;

	@Value("${fortscale.aggregation.feature.extraction.feature_extract_service.feature_conf_json:}")
	String featuresConfJasonFileName;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isNotBlank(featuresConfJasonFileName)){
			setFeaturesConfJsonFromFile(featuresConfJasonFileName);
			for(String featureName: featuresConfJson.keySet()){
				createFeatureExtractor(featureName);
			}
		}
	}

	private void setFeaturesConfJsonFromFile(String fileName) throws IllegalArgumentException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(fileName));
			featuresConfJson = (JSONObject) jsonObj.get(JSON_CONF_FEATURE_CONFS_NODE_NAME);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to read json conf file %s", fileName);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	private FeatureExtractor getFeatureExtractor(String featureName) {
		return featureExtractorMap.get(featureName);
	}

	private FeatureExtractor createFeatureExtractor(String featureName) {
		FeatureExtractor featureExtractor = null;
		if(featuresConfJson != null){
			JSONObject featureJson = (JSONObject)featuresConfJson.get(featureName);
	
			if (featureJson == null) {
				String errorMsg = String.format("No such feature name: %s", featureName);
				logger.error(errorMsg);
				return null;
			}
	
			JSONObject extractorJson = (JSONObject) featureJson.get(JSON_CONF_EXTRACTOR_NODE_NAME);
			if (extractorJson == null) {
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
		}
		
		return featureExtractor;

	}

	@Override
	public Feature extract(String featureName, JSONObject eventMessage) {
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

		for (String featureName : featureNames) {
			Feature feature = extract(featureName, message);
			features.put(featureName, feature);
		}
		return features;
	}
}
