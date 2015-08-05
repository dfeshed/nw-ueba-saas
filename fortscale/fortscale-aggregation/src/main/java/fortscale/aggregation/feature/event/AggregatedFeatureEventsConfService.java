package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class AggregatedFeatureEventsConfService implements InitializingBean, ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsConfService.class);
	private static final String AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME = "AggregatedFeatureEvents";
	private static final String ARRAY_OF_EVENTS_JSON_FIELD_NAME = "Events";

	@Value("${fortscale.aggregation.feature.event.conf.json.file.name}")
	private String aggregatedFeatureEventConfJsonFilePath;
	
	private ApplicationContext applicationContext;

	@Autowired
	private BucketConfigurationService bucketConfigurationService;

	// List of aggregated feature event configurations
	private List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList;

	@Override
	public void afterPropertiesSet() throws Exception {
		loadAggregatedFeatureEventDefinitions();
		getBucketConfs();
	}

	public List<AggregatedFeatureEventConf> getAggregatedFeatureEventConfList() {
		List<AggregatedFeatureEventConf> returned = new ArrayList<>();
		returned.addAll(aggregatedFeatureEventConfList);
		return returned;
	}

	private void loadAggregatedFeatureEventDefinitions() {
		JSONObject aggregatedFeatureEvents;
		JSONArray arrayOfEvents;
		String errorMsg;

		try {
			JSONObject jsonObject;

			Resource bucketsJsonResource = applicationContext.getResource(aggregatedFeatureEventConfJsonFilePath);
			jsonObject = (JSONObject) JSONValue.parseWithException(bucketsJsonResource.getInputStream());

			aggregatedFeatureEvents = (JSONObject)jsonObject.get(AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME);
		} catch (Exception e) {
			errorMsg = String.format("Failed to parse JSON file %s", aggregatedFeatureEventConfJsonFilePath);
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		}

		if (aggregatedFeatureEvents == null) {
			errorMsg = String.format("JSON file %s does not contain field %s", aggregatedFeatureEventConfJsonFilePath, AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}

		arrayOfEvents = (JSONArray)aggregatedFeatureEvents.get(ARRAY_OF_EVENTS_JSON_FIELD_NAME);
		if (arrayOfEvents == null) {
			errorMsg = String.format("JSON file %s does not contain array %s", aggregatedFeatureEventConfJsonFilePath, ARRAY_OF_EVENTS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}

		aggregatedFeatureEventConfList = new ArrayList<>();
		for (Object event : arrayOfEvents) {
			String confAsString = ((JSONObject)event).toJSONString();
			try {
				AggregatedFeatureEventConf conf = (new ObjectMapper()).readValue(confAsString, AggregatedFeatureEventConf.class);
				aggregatedFeatureEventConfList.add(conf);
			} catch (Exception e) {
				errorMsg = String.format("Failed to deserialize JSON %s", confAsString);
				logger.error(errorMsg, e);
				throw new RuntimeException(errorMsg, e);
			}
		}
	}

	private void getBucketConfs() {
		for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
			String bucketConfName = conf.getBucketConfName();
			FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(bucketConfName);
			conf.setBucketConf(featureBucketConf);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
