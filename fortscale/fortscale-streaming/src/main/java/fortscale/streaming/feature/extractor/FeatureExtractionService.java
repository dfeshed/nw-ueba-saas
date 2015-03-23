package fortscale.streaming.feature.extractor;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

import fortscale.ml.feature.extractor.IFeatureExtractionService;
import fortscale.utils.StringPredicates;
import fortscale.utils.logging.Logger;

public class FeatureExtractionService implements IFeatureExtractionService{
	private static final Logger logger = Logger.getLogger(FeatureExtractionService.class); 
	
	private static final String FEATURE_EXTRACTOR_CONFIG_PREFIX = "fortscale.feature.extractor.";
	private static final String CLASS_JSON_CONFIG_FORMAT = FEATURE_EXTRACTOR_CONFIG_PREFIX + "%s.class.json";
	
	private Map<String, FeatureExtractor> featureExtractorMap = new HashMap<>();

	public FeatureExtractionService(Config config){
		Config fieldsSubset = config.subset(FEATURE_EXTRACTOR_CONFIG_PREFIX);
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".class.name"))) {
			String featureName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".class.name"));
			String featureExtractorClass = getConfigString(config, fieldConfigKey);
			String featureExtractorClassJson = getConfigString(config, String.format(CLASS_JSON_CONFIG_FORMAT, featureName));
			try {
				FeatureExtractor featureExtractor = (FeatureExtractor) (new ObjectMapper()).readValue(featureExtractorClassJson, Class.forName(featureExtractorClass));
				featureExtractorMap.put(featureName, featureExtractor);
			} catch (Exception e) {
				String errorMsg = String.format("Failed to deserialize json %s", featureExtractorClassJson);
				logger.error(errorMsg, e);
				throw new IllegalArgumentException(errorMsg, e);
			}
		}
	}
	
	@Override
	public Object extract(String featureName, JSONObject eventMessage){
		FeatureExtractor featureExtractor = featureExtractorMap.get(featureName);
		if(featureExtractor != null){
			return featureExtractor.extract(eventMessage);
		} else{
			return eventMessage.get(featureName);
		}
	}
}
