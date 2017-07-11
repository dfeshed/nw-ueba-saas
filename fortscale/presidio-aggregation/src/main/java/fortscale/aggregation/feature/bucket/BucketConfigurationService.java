package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads BucketConfs from JSON file.
 * Provides API to get list of related BucketConfs for a given
 * event based on the context fields within the BucketConfs.
 */
public class BucketConfigurationService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(BucketConfigurationService.class);
	public static final String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";

	private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
	private Map<String, List<FeatureBucketConf>> adeEventTypeToListOfBucketConfs = new HashMap<>();

	private String bucketConfJsonFilePath;
	private String bucketConfJsonOverridingFilesPath;
	private String bucketConfJsonAdditionalFilesPath;


	public BucketConfigurationService(String bucketConfJsonFilePath,
									  String bucketConfJsonOverridingFilesPath,String bucketConfJsonAdditionalFilesPath){
		this.bucketConfJsonFilePath =bucketConfJsonFilePath;
		this.bucketConfJsonOverridingFilesPath = bucketConfJsonOverridingFilesPath;
		this.bucketConfJsonAdditionalFilesPath = bucketConfJsonAdditionalFilesPath;
	}

	@Override
	protected String getBaseConfJsonFilesPath() {
		return bucketConfJsonFilePath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return bucketConfJsonOverridingFilesPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return bucketConfJsonAdditionalFilesPath;
	}

	@Override
	protected String getConfNodeName() {
		return JSON_CONF_BUCKET_CONFS_NODE_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObj) {
		String bucketConfJson = jsonObj.toJSONString();
		FeatureBucketConf bucketConf;

		try {
			bucketConf = (new ObjectMapper()).readValue(bucketConfJson, FeatureBucketConf.class);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to deserialize json %s", bucketConfJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}

		try {
			addNewBucketConf(bucketConf);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to add new bucket conf. json: %s", bucketConfJson);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	public Collection<FeatureBucketConf> getFeatureBucketConfs(){
		return bucketConfs.values();
	}

	/**
	 * Get list of FeatureBucketConf by ade event type, strategyName and contextFieldNames
	 * @param adeRecordReader
	 * @param strategyName
	 * @param contextFieldNames
	 * @return list of featureBucketConfs
	 */
	public List<FeatureBucketConf> getRelatedBucketConfs(AdeRecordReader adeRecordReader, String strategyName, List<String> contextFieldNames) {
		if (adeRecordReader == null) return null;
		String adeEventType = adeRecordReader.getAdeEventType();
		if (adeEventType.isEmpty()) return null;
		List<FeatureBucketConf> featureBucketConfs = adeEventTypeToListOfBucketConfs.get(adeEventType);
		featureBucketConfs = featureBucketConfs.stream().filter(featureBucketConf -> featureBucketConf.getStrategyName().equals(strategyName) && featureBucketConf.getContextFieldNames().equals(contextFieldNames)).collect(Collectors.toList());

		return featureBucketConfs;
	}


	public FeatureBucketConf getBucketConf(String bucketConfName) {
		return bucketConfs.get(bucketConfName);
	}

	public boolean isBucketConfExist(String bucketConfName) {
		return bucketConfs.containsKey(bucketConfName);
	}

	public void addNewBucketConf(FeatureBucketConf bucketConf) throws BucketAlreadyExistException {
		FeatureBucketConf existingBucketConf = getBucketConf(bucketConf.getName());

		if (existingBucketConf != null) {
			throw new BucketAlreadyExistException(existingBucketConf, bucketConf);
		}

		bucketConfs.put(bucketConf.getName(), bucketConf);
		List<String> adeEventTypeList = bucketConf.getAdeEventTypes();

		for (String adeEventType : adeEventTypeList) {
			List<FeatureBucketConf> listOfBucketConfs = adeEventTypeToListOfBucketConfs.get(adeEventType);

			if (listOfBucketConfs == null) {
				listOfBucketConfs = new ArrayList<>();
				adeEventTypeToListOfBucketConfs.put(adeEventType, listOfBucketConfs);
			}

			listOfBucketConfs.add(bucketConf);
		}
	}

	public void addNewAggregatedFeatureConfToBucketConf(String bucketConfName, AggregatedFeatureConf aggregatedFeatureConf) {
		FeatureBucketConf featureBucketConf = getBucketConf(bucketConfName);
		featureBucketConf.addAggregatedFeatureConf(aggregatedFeatureConf);
	}

	public Set<List<String>> getRelatedDistinctContexts(String adeEventType){
		List<FeatureBucketConf> featureBucketConfs = adeEventTypeToListOfBucketConfs.get(adeEventType);
		Set<List<String>> distinctContextsSet = new HashSet<>();
		for(FeatureBucketConf featureBucketConf: featureBucketConfs){
			distinctContextsSet.add(featureBucketConf.getContextFieldNames());
		}

		return distinctContextsSet;
	}
}
