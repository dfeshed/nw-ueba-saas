
package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.bucket.BucketAlreadyExistException;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatedFeatureEventsConfService implements InitializingBean, ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsConfService.class);
	private static final String AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME = "AggregatedFeatureEvents";
	private static final String ARRAY_OF_EVENTS_JSON_FIELD_NAME = "Events";
	private static final String ARRAY_OF_RETENTION_STRATEGIES_JSON_FIELD_NAME = "RetentionStrategies";

	@Value("${fortscale.aggregation.feature.event.conf.json.file.name}")
	private String aggregatedFeatureEventConfJsonFilePath;
	
	private ApplicationContext applicationContext;

	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	
	@Autowired
	private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;

	// List of aggregated feature event configurations
	private List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList;
	private Map<String, AggrFeatureRetentionStrategy> aggrFeatureRetentionStrategies;

	@Override
	public void afterPropertiesSet() throws Exception {
		loadAggregatedFeatureRetentionStrategies();
		loadAggregatedFeatureEventDefinitions();
		fillBucketConfs();
		createOutputBuckets();
	}

	public AggregatedFeatureEventConf getAggregatedFeatureEventConf(String name) {
		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
			if (aggregatedFeatureEventConf.getName().equals(name)) {
				return aggregatedFeatureEventConf;
			}
		}

		// nothing found
		return null;
	}

	public List<AggregatedFeatureEventConf> getAggregatedFeatureEventConfList() {
		List<AggregatedFeatureEventConf> returned = new ArrayList<>();
		returned.addAll(aggregatedFeatureEventConfList);
		return returned;
	}

	public String getAnomalyType(String aggregatedFeatureName){
		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
			if (aggregatedFeatureEventConf.getName().equals(aggregatedFeatureName)) {
				return  aggregatedFeatureEventConf.getAnomalyType();
			}
		}

		return null;
	}

	public AggrEventEvidenceFilteringStrategyEnum getEvidenceReadingStrategy(String aggregatedFeatureName){
		String strategy = "";
		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
			if (aggregatedFeatureEventConf.getName().equals(aggregatedFeatureName)) {
				strategy = aggregatedFeatureEventConf.getEvidencesFilterStrategy();
			}
		}

		return AggrEventEvidenceFilteringStrategyEnum.valueOf(strategy);
	}

	private void loadAggregatedFeatureRetentionStrategies() {
		JSONObject retentionStrategies;
		JSONArray arrayOfStrategies;
		String errorMsg;

		try {
			JSONObject jsonObject;

			Resource bucketsJsonResource = applicationContext.getResource(aggregatedFeatureEventConfJsonFilePath);
			jsonObject = (JSONObject) JSONValue.parseWithException(bucketsJsonResource.getInputStream());

			retentionStrategies = (JSONObject)jsonObject.get(AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME);
		} catch (Exception e) {
			errorMsg = String.format("Failed to parse JSON file %s", aggregatedFeatureEventConfJsonFilePath);
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		}

		if (retentionStrategies == null) {
			errorMsg = String.format("JSON file %s does not contain field %s", aggregatedFeatureEventConfJsonFilePath, AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}

		arrayOfStrategies = (JSONArray)retentionStrategies.get(ARRAY_OF_RETENTION_STRATEGIES_JSON_FIELD_NAME);
		if (arrayOfStrategies == null) {
			errorMsg = String.format("JSON file %s does not contain array %s", aggregatedFeatureEventConfJsonFilePath, ARRAY_OF_RETENTION_STRATEGIES_JSON_FIELD_NAME);
			logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}

		aggrFeatureRetentionStrategies = new HashMap();
		for (Object strategy : arrayOfStrategies) {
			String confAsString = ((JSONObject)strategy).toJSONString();
			try {
				AggrFeatureRetentionStrategy aggrFeatureRetentionStrategy = (new ObjectMapper()).readValue(confAsString, AggrFeatureRetentionStrategy.class);
				aggrFeatureRetentionStrategies.put(aggrFeatureRetentionStrategy.getName(), aggrFeatureRetentionStrategy);
			} catch (Exception e) {
				errorMsg = String.format("Failed to deserialize JSON %s", confAsString);
				logger.error(errorMsg, e);
				throw new RuntimeException(errorMsg, e);
			}
		}
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
	
	private void createOutputBuckets(){
		for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
			try {
				createOutputBucket(conf);
			} catch (JsonProcessingException | ParseException | BucketAlreadyExistException e) {
				String errorMsg = String.format("Failed to create output bucket conf. output bucket strategy: %s", conf.getOutputBucketStrategy());
				logger.error(errorMsg, e);
				throw new RuntimeException(errorMsg, e);
			}
		}
	}
	
	private void createOutputBucket(AggregatedFeatureEventConf conf) throws JsonProcessingException, ParseException, BucketAlreadyExistException{
		String outputBucketStrategy = conf.getOutputBucketStrategy();
		if(outputBucketStrategy == null){
			return;
		}
		
		String outputBucketConfName = aggregatedFeatureEventsConfUtilService.buildOutputBucketConfName(conf);
		AggregatedFeatureConf aggregatedFeatureConf = aggregatedFeatureEventsConfUtilService.createOutputAggregatedFeatureConf(conf);
		if(bucketConfigurationService.isBucketConfExist(outputBucketConfName)){
			bucketConfigurationService.addNewAggregatedFeatureConfToBucketConf(outputBucketConfName, aggregatedFeatureConf);
		} else{
			List<String> dataSources = new ArrayList<>();
			dataSources.add(aggregatedFeatureEventsConfUtilService.buildOutputBucketDataSource(conf));
			List<String> contextFieldNames = new ArrayList<>();
			for(String contextName: conf.getBucketConf().getContextFieldNames()){
				contextFieldNames.add(aggregatedFeatureEventsConfUtilService.buildAggregatedFeatureContextFieldName(contextName));
			}
			String strategyName = outputBucketStrategy;
			List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
			aggrFeatureConfs.add(aggregatedFeatureConf);
			FeatureBucketConf featureBucketConf = new FeatureBucketConf(outputBucketConfName, dataSources, contextFieldNames, strategyName, aggrFeatureConfs);
			bucketConfigurationService.addNewBucketConf(featureBucketConf);
		}
	}
	
	

	private void fillBucketConfs() {
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

	public AggrFeatureRetentionStrategy getAggrFeatureRetnetionStrategy(String strategyName) {
		return aggrFeatureRetentionStrategies.get(strategyName);
	}
}
