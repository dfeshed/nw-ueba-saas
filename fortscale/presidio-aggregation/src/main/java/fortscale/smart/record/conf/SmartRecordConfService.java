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

import static fortscale.utils.fixedduration.FixedDurationStrategy.fromStrategyName;

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

	/**
	 * Check if the given {@link AggregatedFeatureEventConf} can be included in the given {@link SmartRecordConf}:
	 * 1. The {@link AggregatedFeatureEventConf} and the {@link SmartRecordConf} must have the same strategy.
	 * 2. The {@link AggregatedFeatureEventConf} and the {@link SmartRecordConf} must have the same number of contexts.
	 * 3. Each list of possible fields for a context in the {@link SmartRecordConf} should have exactly one
	 *    representative in the {@link AggregatedFeatureEventConf}'s list of context field names. If there's
	 *    more than one representative, then the {@link AggregatedFeatureEventConf}'s list of context field
	 *    names is ambiguous, and it's not possible to determine which one should be chosen. In this case an
	 *    {@link IllegalArgumentException} is thrown.
	 * @param aggregatedFeatureEventConf The {@link AggregatedFeatureEventConf}.
	 * @param smartRecordConf            The {@link SmartRecordConf}.
	 * @return True if the {@link AggregatedFeatureEventConf} can be included in the {@link SmartRecordConf}, false otherwise.
	 */
	public static boolean aggregatedFeatureEventConfFitsSmartRecordConf(
			AggregatedFeatureEventConf aggregatedFeatureEventConf, SmartRecordConf smartRecordConf) {

		FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
		FixedDurationStrategy featureBucketStrategy = fromStrategyName(featureBucketConf.getStrategyName());
		FixedDurationStrategy smartRecordStrategy = smartRecordConf.getFixedDurationStrategy();
		String aggregatedFeatureEventName = aggregatedFeatureEventConf.getName();
		String smartRecordName = smartRecordConf.getName();

		if (featureBucketStrategy != smartRecordStrategy) {
			logger.info(
					"The strategy of aggregated feature event {} ({}) is " +
					"different than the strategy of smart record {} ({}).",
					aggregatedFeatureEventName, featureBucketStrategy,
					smartRecordName, smartRecordStrategy);
			return false;
		}

		List<String> featureBucketContextFieldNames = featureBucketConf.getContextFieldNames();
		Map<String, List<String>> smartRecordContextToFieldsMap = smartRecordConf.getContextToFieldsMap();

		if (featureBucketContextFieldNames.size() != smartRecordContextToFieldsMap.size()) {
			logger.info(
					"The number of contexts of aggregated feature event {} ({}) is " +
					"different than the number of contexts of smart record {} ({}).",
					aggregatedFeatureEventName, featureBucketContextFieldNames.size(),
					smartRecordName, smartRecordContextToFieldsMap.size());
			return false;
		}

		for (Map.Entry<String, List<String>> smartRecordContextAndFields : smartRecordContextToFieldsMap.entrySet()) {
			// Create a new list of feature bucket context field names, in order not to change the original one.
			List<String> copyOfFeatureBucketContextFieldNames = new ArrayList<>(featureBucketContextFieldNames);
			copyOfFeatureBucketContextFieldNames.retainAll(smartRecordContextAndFields.getValue());

			if (copyOfFeatureBucketContextFieldNames.isEmpty()) {
				logger.info(
						"The context field names of aggregated feature event {} contain no " +
						"representative from the possible fields of context {} in smart record {}.",
						aggregatedFeatureEventName, smartRecordContextAndFields.getKey(), smartRecordName);
				return false;
			} else if (copyOfFeatureBucketContextFieldNames.size() > 1) {
				String msg = String.format(
						"The context field names of aggregated feature event %s contain more than " +
						"one representative from the possible fields of context %s in smart record %s.",
						aggregatedFeatureEventName, smartRecordContextAndFields.getKey(), smartRecordName);
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
		}

		return true;
	}

	private void validateSmartRecordConf(SmartRecordConf smartRecordConf) {
		for (String aggregationRecordName : smartRecordConf.getAggregationRecordNames()) {
			AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
					.getAggregatedFeatureEventConf(aggregationRecordName);

			if (aggregatedFeatureEventConf == null) {
				String msg = String.format("Aggregation record %s is not configured.", aggregationRecordName);
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}

			if (!aggregatedFeatureEventConfFitsSmartRecordConf(aggregatedFeatureEventConf, smartRecordConf)) {
				String msg = String.format("Aggregation record %s cannot be included in smart record %s.", aggregationRecordName, smartRecordConf.getName());
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
		}
	}

	private void completeClusterConfs(SmartRecordConf smartRecordConf) {
		Collection<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConfs(smartRecordConf);
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
