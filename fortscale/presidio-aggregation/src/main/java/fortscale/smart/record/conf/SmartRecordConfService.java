package fortscale.smart.record.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A service that manages all the {@link SmartRecordConf}s.
 *
 * @author Lior Govrin
 */
public class SmartRecordConfService extends AslConfigurationService {
	private static final Logger logger = Logger.getLogger(SmartRecordConfService.class);
	private static final String SMART_RECORD_CONFS_NODE_NAME = "SmartRecordConfs";

	private String baseConfigurationsPath;
	private String overridingConfigurationsPath;
	private String additionalConfigurationsPath;
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	private ObjectMapper objectMapper;
	private Map<String, SmartRecordConf> nameToSmartRecordConfMap;

	public SmartRecordConfService(
			String baseConfigurationsPath,
			String overridingConfigurationsPath,
			String additionalConfigurationsPath,
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {

		this.baseConfigurationsPath = baseConfigurationsPath;
		this.overridingConfigurationsPath = overridingConfigurationsPath;
		this.additionalConfigurationsPath = additionalConfigurationsPath;
		this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
		this.objectMapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
		this.nameToSmartRecordConfMap = new HashMap<>();
	}

	@Override
	protected String getBaseConfJsonFilesPath() {
		return baseConfigurationsPath;
	}

	@Override
	protected String getBaseOverridingConfJsonFolderPath() {
		return overridingConfigurationsPath;
	}

	@Override
	protected String getAdditionalConfJsonFolderPath() {
		return additionalConfigurationsPath;
	}

	@Override
	protected String getConfNodeName() {
		return SMART_RECORD_CONFS_NODE_NAME;
	}

	@Override
	protected void loadConfJson(JSONObject jsonObject) {
		String jsonString = jsonObject.toJSONString();
		SmartRecordConf smartRecordConf;

		try {
			smartRecordConf = objectMapper.readValue(jsonString, SmartRecordConf.class);
		} catch (Exception e) {
			String msg = String.format("Failed to deserialize JSON string %s.", jsonString);
			logger.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}

		validateSmartRecordConf(smartRecordConf);
		if (smartRecordConf.isIncludeAllAggregationRecords()) completeClusterConfs(smartRecordConf);
		nameToSmartRecordConfMap.put(smartRecordConf.getName(), smartRecordConf);
	}

	/**
	 * @param name the name of the {@link SmartRecordConf}
	 * @return the corresponding {@link SmartRecordConf}
	 */
	public SmartRecordConf getSmartRecordConf(String name) {
		return nameToSmartRecordConfMap.get(name);
	}

	/**
	 * @return a collection of all the {@link SmartRecordConf}s
	 */
	public Collection<SmartRecordConf> getSmartRecordConfs() {
		return nameToSmartRecordConfMap.values();
	}

	/**
	 * @param smartRecordConfName the name of the {@link SmartRecordConf}
	 * @return a set of {@link AggregatedDataPaginationParam}s, one for each aggregation record configured
	 *         in the corresponding {@link SmartRecordConf} (contains the aggregation record's name and type)
	 */
	public Set<AggregatedDataPaginationParam> getPaginationParams(String smartRecordConfName) {
		SmartRecordConf smartRecordConf = nameToSmartRecordConfMap.get(smartRecordConfName);

		if (smartRecordConf == null) {
			logger.error("Smart record conf {} doesn't exist. Returning null pagination params.", smartRecordConfName);
			return null;
		}

		return smartRecordConf.getAggregationRecordNames().stream()
				.map(name -> {
					String type = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(name).getType();
					return new AggregatedDataPaginationParam(name, AggregatedFeatureType.fromCodeRepresentation(type));
				})
				.collect(Collectors.toSet());
	}

	private void validateSmartRecordConf(SmartRecordConf smartRecordConf) {
		String smartRecordName = smartRecordConf.getName();
		String smartRecordStrategyName = smartRecordConf.getFixedDurationStrategy().toStrategyName();

		for (String aggregationRecordName : smartRecordConf.getAggregationRecordNames()) {
			AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
					.getAggregatedFeatureEventConf(aggregationRecordName);

			if (aggregatedFeatureEventConf == null) {
				String msg = String.format("Aggregation record %s is not configured.", aggregationRecordName);
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}

			FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
			String featureBucketStrategyName = featureBucketConf.getStrategyName();

			if (!featureBucketConf.getContextFieldNames().containsAll(smartRecordConf.getContexts())) {
				String msg = String.format("The context field names of smart record %s are not a subset " +
						"of those of aggregation record %s.", smartRecordName, aggregationRecordName);
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}

			if (!featureBucketStrategyName.equals(smartRecordStrategyName)) {
				String msg = String.format("The fixed duration strategy of aggregation record %s " +
						"does not match that of smart record %s.", aggregationRecordName, smartRecordName);
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
		}
	}

	private void completeClusterConfs(SmartRecordConf smartRecordConf) {
		List<String> contexts = smartRecordConf.getContexts();
		FixedDurationStrategy fixedDurationStrategy = smartRecordConf.getFixedDurationStrategy();
		Collection<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConfs(contexts, fixedDurationStrategy);
		List<String> excludedAggregationRecords = smartRecordConf.getExcludedAggregationRecords();
		Set<String> aggregationRecordNames = smartRecordConf.getAggregationRecordNames();

		for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
			String aggregatedFeatureEventConfName = aggregatedFeatureEventConf.getName();

			if (excludedAggregationRecords.contains(aggregatedFeatureEventConfName)) {
				if (aggregationRecordNames.contains(aggregatedFeatureEventConfName)) {
					throw new IllegalArgumentException(String.format(
							"Aggregation record %s is supposed to be excluded, but it is manually " +
							"defined in one of the cluster confs.", aggregatedFeatureEventConfName));
				}
			} else if (!aggregationRecordNames.contains(aggregatedFeatureEventConfName)) {
				List<String> singletonList = Collections.singletonList(aggregatedFeatureEventConfName);
				ClusterConf clusterConf = new ClusterConf(singletonList, smartRecordConf.getDefaultWeight());
				smartRecordConf.getClusterConfs().add(clusterConf);
				aggregationRecordNames.add(aggregatedFeatureEventConfName);
			}
		}
	}
}
