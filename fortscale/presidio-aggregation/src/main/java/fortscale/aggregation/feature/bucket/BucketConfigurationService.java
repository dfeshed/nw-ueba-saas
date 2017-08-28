package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
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
	private static final String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";

	private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
	private Map<String, List<FeatureBucketConf>> adeEventTypeToListOfBucketConfs = new HashMap<>();

	private String bucketConfJsonFilePath;
	private String bucketConfJsonOverridingFilesPath;
	private String bucketConfJsonAdditionalFilesPath;

	public BucketConfigurationService(
			String bucketConfJsonFilePath,
			String bucketConfJsonOverridingFilesPath,
			String bucketConfJsonAdditionalFilesPath) {

		this.bucketConfJsonFilePath = bucketConfJsonFilePath;
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

	/**
	 * @return list of {@link FeatureBucketConf}s by adeEventType, strategyName and contextFieldNames
	 */
	public List<FeatureBucketConf> getRelatedBucketConfs(
			AdeRecordReader adeRecordReader, String strategyName, List<String> contextFieldNames) {

		if (adeRecordReader == null) return null;
		String adeEventType = adeRecordReader.getAdeEventType();
		if (StringUtils.isEmpty(adeEventType)) return null;
		List<FeatureBucketConf> featureBucketConfs = adeEventTypeToListOfBucketConfs.get(adeEventType);
		Assert.notNull(featureBucketConfs, String.format("no feature bucket conf is defined for adeEventType=%s", adeEventType));

		featureBucketConfs = featureBucketConfs.stream()
				.filter(featureBucketConf ->
						featureBucketConf.getStrategyName().equals(strategyName) &&
						featureBucketConf.getContextFieldNames().equals(contextFieldNames))
				.collect(Collectors.toList());

		return featureBucketConfs;
	}

	public FeatureBucketConf getBucketConf(String bucketConfName) {
		return bucketConfs.get(bucketConfName);
	}

	public Set<List<String>> getRelatedDistinctContexts(String adeEventType) {
		List<FeatureBucketConf> featureBucketConfs = adeEventTypeToListOfBucketConfs.get(adeEventType);
		if (featureBucketConfs == null) {
			logger.warn("no feature bucket conf for the given ade event type {}", adeEventType);
			// TODO: Add monitoring metric
			return Collections.emptySet();
		}

		Set<List<String>> distinctContextsSet = new HashSet<>();
		for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
			distinctContextsSet.add(featureBucketConf.getContextFieldNames());
		}

		return distinctContextsSet;
	}

	private void addNewBucketConf(FeatureBucketConf bucketConf) throws BucketAlreadyExistException {
		FeatureBucketConf existingBucketConf = getBucketConf(bucketConf.getName());
		if (existingBucketConf != null) throw new BucketAlreadyExistException(existingBucketConf, bucketConf);
		bucketConfs.put(bucketConf.getName(), bucketConf);
		List<String> adeEventTypeList = bucketConf.getAdeEventTypes();

		for (String adeEventType : adeEventTypeList) {
			List<FeatureBucketConf> listOfBucketConfs = adeEventTypeToListOfBucketConfs
					.computeIfAbsent(adeEventType, key -> new ArrayList<>());
			listOfBucketConfs.add(bucketConf);
		}
	}
}
