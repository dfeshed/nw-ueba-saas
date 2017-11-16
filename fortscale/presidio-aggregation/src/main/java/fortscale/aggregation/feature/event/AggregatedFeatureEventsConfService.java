package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.aggregation.exceptions.AggregatedFeatureEventConfNameMissingInBucketsException;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.*;

public class AggregatedFeatureEventsConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(AggregatedFeatureEventsConfService.class);
	private static final String AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME = "AggregatedFeatureEvents";

	private String aggregatedFeatureEventsBaseConfigurationPath;
	private String aggregatedFeatureEventsOverridingConfigurationPath;
	private String aggregatedFeatureEventsAdditionalConfigurationPath;
	private BucketConfigurationService bucketConfigurationService;

	private ObjectMapper objectMapper;
	private List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = new ArrayList<>();
	private Map<String, List<AggregatedFeatureEventConf>> bucketConfName2FeatureEventConfMap = new HashMap<>();

	public AggregatedFeatureEventsConfService(
			String aggregatedFeatureEventsBaseConfigurationPath,
			String aggregatedFeatureEventsOverridingConfigurationPath,
			String aggregatedFeatureEventsAdditionalConfigurationPath,
			BucketConfigurationService bucketConfigurationService) {

		this.objectMapper= ObjectMapperProvider.getInstance().getDefaultObjectMapper();
		this.aggregatedFeatureEventsBaseConfigurationPath = aggregatedFeatureEventsBaseConfigurationPath;
		this.aggregatedFeatureEventsOverridingConfigurationPath = aggregatedFeatureEventsOverridingConfigurationPath;
		this.aggregatedFeatureEventsAdditionalConfigurationPath = aggregatedFeatureEventsAdditionalConfigurationPath;
		this.bucketConfigurationService = bucketConfigurationService;
	}

	@Override
	protected String getBaseConfJsonFilesPath() {
		return aggregatedFeatureEventsBaseConfigurationPath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return aggregatedFeatureEventsOverridingConfigurationPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return aggregatedFeatureEventsAdditionalConfigurationPath;
	}

	@Override
	protected String getConfNodeName() {
		return AGGREGATED_FEATURE_EVENTS_JSON_FIELD_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObject) {
		String jsonString = jsonObject.toJSONString();

		try {
			AggregatedFeatureEventConf conf = objectMapper.readValue(jsonString, AggregatedFeatureEventConf.class);
			aggregatedFeatureEventConfList.add(conf);
		} catch (Exception e) {
			String msg = String.format("Failed to deserialize JSON string %s to %s.",
					jsonString, AggregatedFeatureEventConf.class.getSimpleName());
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		fillBucketConfs();
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

	public List<AggregatedFeatureEventConf> getAggregatedFeatureEventConfList(String bucketConfName) {
		List<AggregatedFeatureEventConf> ret = bucketConfName2FeatureEventConfMap.get(bucketConfName);
		return ret != null ? ret : Collections.emptyList();
	}

	public String getAnomalyType(String aggregatedFeatureName) {
		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
			if (aggregatedFeatureEventConf.getName().equals(aggregatedFeatureName)) {
				return aggregatedFeatureEventConf.getAnomalyType();
			}
		}

		// nothing found
		return null;
	}

	public Collection<AggregatedFeatureEventConf> getAggregatedFeatureEventConfs(
			List<String> contextFieldNames, FixedDurationStrategy fixedDurationStrategy) {

		Set<String> requiredContextFieldNames = new HashSet<>(contextFieldNames);
		String requiredStrategyName = fixedDurationStrategy.toStrategyName();
		Collection<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = new LinkedList<>();

		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
			FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
			Set<String> actualContextFieldNames = new HashSet<>(featureBucketConf.getContextFieldNames());
			String actualStrategyName = featureBucketConf.getStrategyName();

			if (actualContextFieldNames.equals(requiredContextFieldNames) && actualStrategyName.equals(requiredStrategyName)) {
				aggregatedFeatureEventConfs.add(aggregatedFeatureEventConf);
			}
		}

		return aggregatedFeatureEventConfs;
	}

	private void fillBucketConfs() {
		for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
			String bucketConfName = conf.getBucketConfName();
			FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(bucketConfName);

			if (featureBucketConf == null) {
				throw new AggregatedFeatureEventConfNameMissingInBucketsException(bucketConfName);
			}

			conf.setBucketConf(featureBucketConf);
			List<AggregatedFeatureEventConf> bucketAggrFeatureEventConfList = bucketConfName2FeatureEventConfMap
					.computeIfAbsent(bucketConfName, key -> new ArrayList<>());
			bucketAggrFeatureEventConfList.add(conf);
		}
	}
}
