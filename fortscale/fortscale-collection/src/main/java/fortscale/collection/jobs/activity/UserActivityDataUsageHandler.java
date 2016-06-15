package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataUsageConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityDataUsageDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserActivityDataUsageHandler extends UserActivityBaseHandler implements InitializingBean {

	private static Logger logger = Logger.getLogger(UserActivityDataUsageHandler.class);

	private static final UserActivityType ACTIVITY = UserActivityType.DATA_USAGE;

	private static final String AGGREGATED_FEATURES_PREFIX = "aggregatedFeatures";
	private static final String DOT_REPLACEMENT = "#dot#";

	@Autowired
	private UserActivityDataUsageConfigurationService userActivityDataUsageConfigurationService;

	private Map<String, String> collectionToHistogram;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		if (collectionToHistogram.containsKey(dataSourceLowerCase)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_PREFIX + "." + collectionToHistogram.
					get(dataSourceLowerCase)));
		}
		throw new IllegalArgumentException("Invalid data source: " + dataSource);
	}

	@Override
	protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
		GenericHistogram histogram = new GenericHistogram();
		if (objectToConvert == null) {
			return histogram;
		}
		if (objectToConvert instanceof Feature) {
			final GenericHistogram genericHistogram = (GenericHistogram)((Feature)objectToConvert).getValue();
			Double total = 0.0;
			for (String key: genericHistogram.getHistogramMap().keySet()) {
				key = key.replaceAll(DOT_REPLACEMENT, ".");
				if (StringUtils.isNumeric(key)) {
					total += Double.parseDouble(key);
				} else {
					total += genericHistogram.getHistogramMap().get(key);
				}
			}
			histogram.add(getKeysByValue(collectionToHistogram, histogramFeatureName) + "." + histogramFeatureName,
					total);
		} else {
			String errorMessage = String.format("Can't convert object %s object of class %s to histogram",
					objectToConvert, objectToConvert.getClass());
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return histogram;
	}

	@Override
	protected List<Class> getRelevantDocumentClasses () {
		return new ArrayList(Collections.singletonList(UserActivityDataUsageDocument.class));
	}

	@Override
	protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources,
			long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {}

	@Override
	protected String getCollectionName() {
		return UserActivityDataUsageDocument.COLLECTION_NAME;
	}

	@Override
	protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
		return new ArrayList(collectionToHistogram.values());
	}

	@Override
	public UserActivityType getActivity() {
		return ACTIVITY;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivityDataUsageConfigurationService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		collectionToHistogram = userActivityDataUsageConfigurationService.getCollectionToHistogram();
	}

	private static <T,E> Set<T> getKeysByValue(Map<T,E> map, E value) {
		return map.entrySet()
			.stream()
			.filter(entry -> Objects.equals(entry.getValue(), value))
			.map(Map.Entry::getKey)
			.collect(Collectors.toSet());
	}

}