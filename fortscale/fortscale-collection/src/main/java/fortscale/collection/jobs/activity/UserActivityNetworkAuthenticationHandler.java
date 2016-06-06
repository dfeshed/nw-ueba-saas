package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfiguration;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.domain.core.OrganizationActivityLocation;
import fortscale.domain.core.UserActivityJobState;
import fortscale.domain.core.UserActivityLocation;
import fortscale.domain.core.UserActivityNetworkAuthentication;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserActivityNetworkAuthenticationHandler extends UserActivityBaseHandler {

	private static Logger logger = Logger.getLogger(UserActivityNetworkAuthenticationHandler.class);

	private static final String ACTIVITY_NAME = "source_devices";

	private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.source_machines";
	private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "src_machines_histogram";


	protected UserActivityNetworkAuthenticationConfigurationService userActivityNetworkAuthenticationConfigurationService;

	public void calculate(int numOfLastDaysToCalculate) {
		long endTime = System.currentTimeMillis();
		long startingTime = TimestampUtils.toStartOfDay(TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate));
		long fullExecutionStartTime = System.nanoTime();

		UserActivityJobState userActivityJobState = loadAndUpdateJobState(numOfLastDaysToCalculate);
		UserActivityConfiguration userActivityConfiguration = userActivityNetworkAuthenticationConfigurationService.getUserActivityConfiguration();
		List<String> dataSources = userActivityConfiguration.getDataSources();
		logger.info("Relevant data sources for network authentication activity: {}", dataSources);

	}

	@Override
	protected void removeRelatedDocuments(Object startingTime) {
		Query query = new Query();
		query.addCriteria(Criteria.where(UserActivityNetworkAuthentication.START_TIME_FIELD_NAME).lt(startingTime));

		mongoTemplate.remove(query, UserActivityLocation.class);

		query = new Query();
		query.addCriteria(Criteria.where(OrganizationActivityLocation.START_TIME_FIELD_NAME).lt(startingTime));
		mongoTemplate.remove(query, OrganizationActivityLocation.class);
	}

	@Override
	public String getActivityName() {
		return ACTIVITY_NAME;
	}
}
