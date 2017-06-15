package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.common.event.Event;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * Loads BucketConfs from JSON file.
 * Provides API to get list of related BucketConfs for a given
 * event based on the context fields within the BucketConfs.
 */
public class BucketConfigurationService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(BucketConfigurationService.class);
	public static final String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";

	private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
	private Map<String, List<FeatureBucketConf>> dataSourceToListOfBucketConfs = new HashMap<>();

	@Value("${impala.table.fields.data.source:data_source}")
	private String dataSourceFieldName;
	@Value("${fortscale.aggregation.bucket.conf.json.file.name:}")
	private String bucketConfJsonFilePath;
	@Value("${fortscale.aggregation.bucket.conf.json.overriding.files.path:}")
	private String bucketConfJsonOverridingFilesPath;
	@Value("${fortscale.aggregation.bucket.conf.json.additional.files.path:}")
	private String bucketConfJsonAdditionalFilesPath;

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

	public List<FeatureBucketConf> getRelatedBucketConfs(Event event) {
		if (event == null) return null;
		Object dataSourceObj = event.get(dataSourceFieldName);
		if (dataSourceObj == null) return null;
		String dataSource = dataSourceObj.toString();
		if (dataSource.isEmpty()) return null;
		return dataSourceToListOfBucketConfs.get(dataSource);
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
		List<String> dataSources = bucketConf.getDataSources();

		for (String s : dataSources) {
			List<FeatureBucketConf> listOfBucketConfs = dataSourceToListOfBucketConfs.get(s);

			if (listOfBucketConfs == null) {
				listOfBucketConfs = new ArrayList<>();
				dataSourceToListOfBucketConfs.put(s, listOfBucketConfs);
			}

			listOfBucketConfs.add(bucketConf);
		}
	}

	public void addNewAggregatedFeatureConfToBucketConf(String bucketConfName, AggregatedFeatureConf aggregatedFeatureConf) {
		FeatureBucketConf featureBucketConf = getBucketConf(bucketConfName);
		featureBucketConf.addAggregatedFeatureConf(aggregatedFeatureConf);
	}
}
