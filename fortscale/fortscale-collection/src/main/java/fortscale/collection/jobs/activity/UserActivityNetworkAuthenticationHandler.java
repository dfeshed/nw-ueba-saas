package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserActivityNetworkAuthenticationHandler extends UserActivityBaseHandler {

	private static final String AGGREGATED_FEATURES_SUCCESS_EVENTS_COUNTER = "aggregatedFeatures.success_events_counter";
	private static final String AGGREGATED_FEATURES_FAILURE_EVENTS_COUNTER = "aggregatedFeatures.failure_events_counter";
	private static final String ACTIVITY_NAME = "network_authentication";
	private static final String AUTHENTICATION_HISTOGRAM_FEATURE_NAME = "authentication_histogram";
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
				return new ArrayList<>(Arrays.asList(AGGREGATED_FEATURES_SUCCESS_EVENTS_COUNTER, AGGREGATED_FEATURES_FAILURE_EVENTS_COUNTER));
		}
		else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
	}

	@Override
	protected void removeRelevantDocuments (Object startingTime){
		Query query = new Query();
		query.addCriteria(Criteria.where(UserActivityNetworkAuthenticationDocument.START_TIME_FIELD_NAME).lt(startingTime));

		mongoTemplate.remove(query, UserActivityLocationDocument.class);

		query = new Query();
		query.addCriteria(Criteria.where(OrganizationActivityLocationDocument.START_TIME_FIELD_NAME).lt(startingTime));
		mongoTemplate.remove(query, OrganizationActivityLocationDocument.class);
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
	protected String getHistogramFeatureName() {
		return AUTHENTICATION_HISTOGRAM_FEATURE_NAME;
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
