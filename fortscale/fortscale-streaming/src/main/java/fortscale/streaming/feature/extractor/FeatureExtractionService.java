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
	private static final String FEATURE_EXTRACTOR_CLASS_JSON_CONFIG_SUFFIX = ".class.json";

	
	private Map<String, FeatureExtractor> featureExtractorMap = new HashMap<>();

	public FeatureExtractionService(Config config) {
		this(config, FEATURE_EXTRACTOR_CONFIG_PREFIX);
	}

	public FeatureExtractionService(Config config, String propertyPrefix){
		Config fieldsSubset = config.subset(propertyPrefix);
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(FEATURE_EXTRACTOR_CLASS_JSON_CONFIG_SUFFIX))) {
			String featureName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(FEATURE_EXTRACTOR_CLASS_JSON_CONFIG_SUFFIX));
			String classJsonConfigFormat = propertyPrefix + "%s" + FEATURE_EXTRACTOR_CLASS_JSON_CONFIG_SUFFIX;
			String featureExtractorClassJson = getConfigString(config, String.format(classJsonConfigFormat, featureName));
			try {
				FeatureExtractor featureExtractor = (new ObjectMapper()).readValue(featureExtractorClassJson, FeatureExtractor.class);

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
