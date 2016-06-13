package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.collection.services.UserActivitySourceMachineConfigurationService;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserActivitySourceMachineHandler extends UserActivityBaseHandler {


	private static Logger logger = Logger.getLogger(UserActivitySourceMachineHandler.class);

	private static final String SOURCE_MACHINE_HISTOGRAM_FEATURE_NAME = "normalized_src_machine_histogram";
	private static final UserActivityType ACTIVITY = UserActivityType.SOURCE_MACHINE;

	@Autowired
	private UserActivitySourceMachineConfigurationService userActivitySourceMachineConfigurationService;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		UserActivitySourceMachineConfigurationService.ActivityDataSourceConfiguration conf = userActivitySourceMachineConfigurationService.getActivityDataSourceConfigurationMap().get(dataSource);
		if (conf != null) {
			return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
		}
		else {
			throw new IllegalArgumentException("Invalid data source: " + dataSource);
		}
	}


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
	protected List<Class> getRelevantDocumentClasses () {
		return new ArrayList<>(Collections.singletonList(UserActivityNetworkAuthenticationDocument.class));
	}

	@Override
	protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Integer> additionalActivityHistogram) {
		//do nothing
	}

	@Override
	protected String getCollectionName() {
		return UserActivitySourceMachineDocument.COLLECTION_NAME;
	}

	@Override
	protected List<String> getHistogramFeatureNames() {
		return new ArrayList<>(Arrays.asList(SOURCE_MACHINE_HISTOGRAM_FEATURE_NAME));
	}

	@Override
	public UserActivityType getActivity() {
		return ACTIVITY;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivitySourceMachineConfigurationService;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
