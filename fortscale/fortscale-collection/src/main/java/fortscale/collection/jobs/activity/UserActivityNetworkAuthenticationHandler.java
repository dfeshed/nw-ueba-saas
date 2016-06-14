package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserActivityNetworkAuthenticationHandler extends UserActivityBaseHandler {

	private static final String ACTIVITY_NAME = "network_authentication";
	private static final String AUTHENTICATION_HISTOGRAM_FEATURE_NAME_SUCCESS = "success_events_counter";
	private static final String AUTHENTICATION_HISTOGRAM_FEATURE_NAME_FAILURE = "failure_events_counter";
	private static final String AGGREGATED_FEATURES_EVENTS_COUNTER_SUCCESS = "aggregatedFeatures." + AUTHENTICATION_HISTOGRAM_FEATURE_NAME_SUCCESS;
	private static final String AGGREGATED_FEATURES_EVENTS_COUNTER_FAILURE = "aggregatedFeatures." + AUTHENTICATION_HISTOGRAM_FEATURE_NAME_FAILURE;

	private static Logger logger = Logger.getLogger(UserActivityNetworkAuthenticationHandler.class);

	@Autowired
	private UserActivityNetworkAuthenticationConfigurationService userActivityNetworkAuthenticationConfigurationService;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		if (dataSourceLowerCase.equals(UserActivityNetworkAuthenticationConfigurationService.DATA_SOURCE_CRMSF_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityNetworkAuthenticationConfigurationService.DATA_SOURCE_SSH_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityNetworkAuthenticationConfigurationService.DATA_SOURCE_KERBEROS_LOGINS_PROPERTY_NAME) ||
				dataSourceLowerCase.equals(UserActivityNetworkAuthenticationConfigurationService.DATA_SOURCE_ORACLE_PROPERTY_NAME)) {
			return new ArrayList<>(Arrays.asList(AGGREGATED_FEATURES_EVENTS_COUNTER_SUCCESS, AGGREGATED_FEATURES_EVENTS_COUNTER_FAILURE));
		}
		else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
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
				case AUTHENTICATION_HISTOGRAM_FEATURE_NAME_SUCCESS:
					histogram.add(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_SUCCESSES, (Double) ((AggrFeatureValue) featureValue).getValue());
					break;
				case AUTHENTICATION_HISTOGRAM_FEATURE_NAME_FAILURE:
					histogram.add(UserActivityNetworkAuthenticationDocument.FIELD_NAME_HISTOGRAM_FAILURES, (Double) ((AggrFeatureValue) featureValue).getValue());
					break;
				default:
					String errorMessage = String.format("Can't convert object %s to histogram. value is invalid: %s", objectToConvert, ((AggrFeatureValue) featureValue).getValue());
					getLogger().error(errorMessage);
					throw new RuntimeException(errorMessage);
			}
		}
		else {
			String errorMessage = String.format("Can't convert object %s object of class %s to histogram", objectToConvert, objectToConvert.getClass());
			getLogger().error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return histogram;
	}

	@Override
	protected List<Class> getRelevantDocumentClasses () {
		return new ArrayList<>(Collections.singletonList(UserActivityNetworkAuthenticationDocument.class));
	}

	@Override
	protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Integer> additionalActivityHistogram) {
		//do nothing
	}

	@Override
	protected String getCollectionName() {
		return UserActivityNetworkAuthenticationDocument.COLLECTION_NAME;
	}


	@Override
	protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
		return new ArrayList<>(Arrays.asList(AUTHENTICATION_HISTOGRAM_FEATURE_NAME_SUCCESS, AUTHENTICATION_HISTOGRAM_FEATURE_NAME_FAILURE));
	}

	@Override
	public String getActivityName() {
		return ACTIVITY_NAME;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivityNetworkAuthenticationConfigurationService;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
