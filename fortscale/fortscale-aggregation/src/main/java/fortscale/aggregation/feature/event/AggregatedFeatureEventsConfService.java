package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.exceptions.AggregatedFeatureEventConfNameMissingInBucketsException;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.bucket.BucketAlreadyExistException;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class AggregatedFeatureEventsConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsConfService.class);
	private static final String AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME = "AggregatedFeatureEvents";

	@Value("${fortscale.aggregation.feature.event.conf.json.file.name}")
	private String aggregatedFeatureEventConfJsonFilePath;
	@Value("${fortscale.aggregation.feature.event.conf.json.overriding.files.path}")
	private String aggregatedFeatureEventConfJsonOverridingFilesPath;
	@Value("${fortscale.aggregation.feature.event.conf.json.additional.files.path}")
	private String aggregatedFeatureEventConfJsonAdditionalFilesPath;

	@Autowired
	private BucketConfigurationService bucketConfigurationService;

	@Autowired
	private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;

	@Autowired
	private RetentionStrategiesConfService retentionStrategiesConfService;

	// List of aggregated feature event configurations
	private List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = new ArrayList<>();
	private Map<String, List<AggregatedFeatureEventConf>> bucketConfName2FeatureEventConfMap = new HashMap<>();

	@Override
	protected String getBaseConfJsonFilesPath() {
		return aggregatedFeatureEventConfJsonFilePath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return aggregatedFeatureEventConfJsonOverridingFilesPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return aggregatedFeatureEventConfJsonAdditionalFilesPath;
	}

	@Override
	protected String getConfNodeName() {
		return AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObj){
		String confAsString = jsonObj.toJSONString();
		try {
			AggregatedFeatureEventConf conf = (new ObjectMapper()).readValue(confAsString, AggregatedFeatureEventConf.class);
			aggregatedFeatureEventConfList.add(conf);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize JSON %s", confAsString);
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg, e);
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
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

	public List<AggregatedFeatureEventConf> getAggregatedFeatureEventConfList(String bucketConfName){
		List<AggregatedFeatureEventConf> ret = bucketConfName2FeatureEventConfMap.get(bucketConfName);

		return ret != null ? ret : Collections.<AggregatedFeatureEventConf>emptyList();
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
			if(featureBucketConf==null)
			{
				throw new AggregatedFeatureEventConfNameMissingInBucketsException(bucketConfName);
			}
			conf.setBucketConf(featureBucketConf);

			List<AggregatedFeatureEventConf> bucketAggFeatureEventConfList = bucketConfName2FeatureEventConfMap.get(bucketConfName);
			if(bucketAggFeatureEventConfList == null){
				bucketAggFeatureEventConfList = new ArrayList<>();
				bucketConfName2FeatureEventConfMap.put(bucketConfName, bucketAggFeatureEventConfList);
			}

			bucketAggFeatureEventConfList.add(conf);
		}
	}


	public AggrFeatureRetentionStrategy getAggrFeatureRetnetionStrategy(String strategyName) {
		return retentionStrategiesConfService.getAggrFeatureRetentionStrategy(strategyName);
	}
}
