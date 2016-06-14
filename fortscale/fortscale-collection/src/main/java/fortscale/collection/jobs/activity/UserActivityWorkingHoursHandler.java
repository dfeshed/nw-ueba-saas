package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityWorkingHoursConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityWorkingHoursDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserActivityWorkingHoursHandler extends UserActivityBaseHandler {

	private static final String ACTIVITY_NAME = "working_hours";
	private static final String FEATURE_NAME_DATE_TIME_UNIX = "date_time_unix_histogram";
	private static final String AGGREGATED_FEATURES_HISTOGRAM = "aggregatedFeatures." + FEATURE_NAME_DATE_TIME_UNIX;
	private static Logger logger = Logger.getLogger(UserActivityWorkingHoursHandler.class);

	@Autowired
	private UserActivityWorkingHoursConfigurationService userActivityWorkingHoursConfigurationService;

	@Override
	protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
		if (objectToConvert instanceof Feature && ((Feature) objectToConvert).getValue() instanceof GenericHistogram) {
			return (GenericHistogram) ((Feature) objectToConvert).getValue();
		}
		else {
			final String errorMessage = String.format("Can't convert %s object of class %s", objectToConvert, objectToConvert.getClass());
			getLogger().error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	@Override
	protected String getCollectionName() {
		return UserActivityWorkingHoursDocument.COLLECTION_NAME;
	}

	@Override
	protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
		return Collections.singletonList(FEATURE_NAME_DATE_TIME_UNIX);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected String getActivityName() {
		return ACTIVITY_NAME;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivityWorkingHoursConfigurationService;
	}

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		if (dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_CRMSF_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_SSH_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_ORACLE_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_KERBEROS_TGT_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_GWAME_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_WAME_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_PRNLOG_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_NTLM_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityWorkingHoursConfigurationService.DATA_SOURCE_VPN_PROPERTY_NAME)) {
			return Collections.singletonList(AGGREGATED_FEATURES_HISTOGRAM);
		}
		else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
	}

	@Override
	protected List<Class> getRelevantDocumentClasses() {
		return Collections.singletonList(UserActivityWorkingHoursDocument.class);
	}

	@Override
	protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {

	}
}
