package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataUsageConfigurationService;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDataUsageDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserActivityDataUsageHandler extends UserActivityBaseHandler {

	private static Logger logger = Logger.getLogger(UserActivityDataUsageHandler.class);

	private static final String ACTIVITY_NAME = "data_usage";
	private static final String AGGREGATED_FEATURES_FILE_SIZE_HISTOGRAM = "aggregatedFeatures.file_size_histogram";
	private static final String AGGREGATED_FEATURES_DB_OBJECT_HISTOGRAM = "aggregatedFeatures.db_object_histogram";
	private static final String AGGREGATED_FEATURES_DATABUCKET_HISTOGRAM = "aggregatedFeatures.databucket_histogram";

	@Autowired
	private UserActivityDataUsageConfigurationService userActivityDataUsageConfigurationService;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_VPN_SESSION_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_DATABUCKET_HISTOGRAM));
		} else if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_ORACLE_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_DB_OBJECT_HISTOGRAM));
		} else if (dataSourceLowerCase.equals(UserActivityDataUsageConfigurationService.
				DATA_SOURCE_PRINT_LOG_PROPERTY_NAME)) {
			return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_FILE_SIZE_HISTOGRAM));
		} else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
	}

	@Override
	protected void removeRelevantDocuments(Object startingTime) {
		Query query = new Query();
		query.addCriteria(Criteria.where(UserActivityNetworkAuthenticationDocument.START_TIME_FIELD_NAME).lt(startingTime));
		mongoTemplate.remove(query, UserActivityLocationDocument.class);
	}

	@Override
	protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
		GenericHistogram histogram = new GenericHistogram();
		if (objectToConvert == null) { //this is legitimate scenario (e.g no failures happened)
			return histogram;
		}
		if (objectToConvert instanceof Feature && ((Feature) objectToConvert).getValue() instanceof AggrFeatureValue) {
			final FeatureValue featureValue = ((Feature) objectToConvert).getValue();
			switch (histogramFeatureName) {
				case AGGREGATED_FEATURES_DATABUCKET_HISTOGRAM:
					histogram.add(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES,
							(Double)((AggrFeatureValue)featureValue).getValue());
					break;
				case AGGREGATED_FEATURES_DB_OBJECT_HISTOGRAM:
					histogram.add(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES,
							(Double)((AggrFeatureValue)featureValue).getValue());
					break;
				case AGGREGATED_FEATURES_FILE_SIZE_HISTOGRAM:
					histogram.add(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES,
							(Double)((AggrFeatureValue)featureValue).getValue());
					break;
				default:
					String errorMessage = String.format("Can't convert object %s to histogram. value is invalid: %s",
							objectToConvert, ((AggrFeatureValue) featureValue).getValue());
					getLogger().error(errorMessage);
					throw new RuntimeException(errorMessage);
			}
		} else {
			String errorMessage = String.format("Can't convert object %s object of class %s to histogram", objectToConvert, objectToConvert.getClass());
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

	@Override
	protected List<String> getHistogramFeatureNames() {
		return new ArrayList(Arrays.asList(AGGREGATED_FEATURES_FILE_SIZE_HISTOGRAM,
				AGGREGATED_FEATURES_DATABUCKET_HISTOGRAM, AGGREGATED_FEATURES_DB_OBJECT_HISTOGRAM));
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