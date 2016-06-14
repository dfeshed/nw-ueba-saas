package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataUsageConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityDataUsageDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserActivityDataUsageHandler extends UserActivityBaseHandler {

	private static Logger logger = Logger.getLogger(UserActivityDataUsageHandler.class);

	private static final String ACTIVITY_NAME = "data_usage";
	private static final String AGGREGATED_FEATURES_PREFIX = "aggregatedFeatures";
	private static final String FILE_SIZE_HISTOGRAM = "file_size_histogram";
	private static final String DB_OBJECT_HISTOGRAM = "db_object_histogram";
	private static final String DATABUCKET_HISTOGRAM = "databucket_histogram";
	private static final String DOT_REPLACEMENT = "#dot#";

	@Autowired
	private UserActivityDataUsageConfigurationService userActivityDataUsageConfigurationService;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_VPN_SESSION_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_PREFIX + "." + DATABUCKET_HISTOGRAM));
		} else if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_ORACLE_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_PREFIX + "." + DB_OBJECT_HISTOGRAM));
		} else if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_PRINT_LOG_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_PREFIX + "." + FILE_SIZE_HISTOGRAM));
		} else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
	}

	@Override
	protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
		GenericHistogram histogram = new GenericHistogram();
		if (objectToConvert == null) {
			return histogram;
		}
		if (objectToConvert instanceof Feature) {
			final GenericHistogram genericHistogram = (GenericHistogram)((Feature)objectToConvert).getValue();
			switch (histogramFeatureName) {
				case DATABUCKET_HISTOGRAM:
				case FILE_SIZE_HISTOGRAM: {
					Double total = 0.0;
					for (String key: genericHistogram.getHistogramMap().keySet()) {
						try {
							total += Double.parseDouble(key.replaceAll(DOT_REPLACEMENT, "."));
						} catch (Exception ex) {
							//key is not a number
						}
					}
					histogram.add(histogramFeatureName, total);
					break;
				} case DB_OBJECT_HISTOGRAM: {
					histogram.add(histogramFeatureName, genericHistogram.getTotalCount());
					break;
				} default: {
					String errorMessage = String.format("Can't convert object %s to histogram. value is invalid: %s",
							objectToConvert, genericHistogram);
					getLogger().error(errorMessage);
					throw new RuntimeException(errorMessage);
				}
			}
		} else {
			String errorMessage = String.format("Can't convert object %s object of class %s to histogram",
					objectToConvert, objectToConvert.getClass());
			getLogger().error(errorMessage);
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
			long currBucketStartTime, long currBucketEndTime, Map<String, Integer> additionalActivityHistogram) {
		//do nothing
	}

	@Override
	protected String getCollectionName() {
		return UserActivityDataUsageDocument.COLLECTION_NAME;
	}

	@Override protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
		return new ArrayList(Arrays.asList(FILE_SIZE_HISTOGRAM, DATABUCKET_HISTOGRAM, DB_OBJECT_HISTOGRAM));
	}

	@Override
	public String getActivityName() {
		return ACTIVITY_NAME;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivityDataUsageConfigurationService;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}