package fortscale.common.feature.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class FeatureExtractService implements IFeatureExtractService, InitializingBean, ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(FeatureExtractService.class);

	private static final String JSON_CONF_FEATURE_CONFS_NODE_NAME = "FeatureConfs";
	private static final String JSON_CONF_EXTRACTOR_NODE_NAME = "extractor";

	private ApplicationContext applicationContext;

	private Map<String, FeatureExtractor> featureExtractorMap = new HashMap<>();
	private JSONObject featuresConfJson;

	@Value("${fortscale.common.feature.extraction.feature_extract_service.feature_conf_json:}")
	String featuresConfJsonFileName;
	
	
	
	


	@Override
	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isNotBlank(featuresConfJsonFileName)){
			setFeaturesConfJsonFromFile();
			for(String featureName: featuresConfJson.keySet()){
				createFeatureExtractor(featureName);
			}
		}
	}

	private void setFeaturesConfJsonFromFile() throws IllegalArgumentException {
		try {
			Resource featuresConfJsonResource = applicationContext.getResource(featuresConfJsonFileName);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(featuresConfJsonResource.getInputStream());
			featuresConfJson = (JSONObject) jsonObj.get(JSON_CONF_FEATURE_CONFS_NODE_NAME);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to parse JSON file %s", featuresConfJsonFileName);
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
	public Feature extract(String featureName, Event event) {
		Feature ret = null;

		try{
			ret = extractWithException(featureName, event);
		} catch (Exception e) {
			logger.error(String.format("Got exception while trying to extract the feature name %s", featureName),e);
		}
		
		return ret;
	}
	
	
	
	private Feature extractWithException(String featureName, Event event) throws Exception {
		FeatureExtractor featureExtractor = getFeatureExtractor(featureName);

		FeatureValue value = null;

		if(featureExtractor != null){
			value = featureExtractor.extract(event);
		} else {
			Object valueObj = event.get(featureName);
			if(valueObj!=null) {
				if(valueObj instanceof String) {
					value = new FeatureStringValue((String)valueObj);
				} else if(valueObj instanceof Number) {
					value = new FeatureNumericValue((Number)valueObj);
				}
			}
		}
		return new Feature(featureName, value);

	}

	@Override
	public Map<String, Feature> extract(Set<String> featureNames, Event event) {
		if(featureNames == null || event == null) {
			return null;
		}
		
		Map<String, Feature> features = new HashMap<>();
		for (String featureName : featureNames) {
			Feature feature;
			try {
				feature = extractWithException(featureName, event);
				features.put(featureName, feature);
			} catch (Exception e) {
				logger.error(String.format("Got exception while trying to extract the feature name %s", featureName),e);
				return null;
			}
		}
		return features;
	}

	@Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
