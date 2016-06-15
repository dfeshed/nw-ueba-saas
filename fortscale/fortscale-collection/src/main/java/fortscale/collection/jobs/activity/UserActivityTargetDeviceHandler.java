package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivityTargetDeviceConfigurationService;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivityTargetDeviceDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class UserActivityTargetDeviceHandler extends UserActivityBaseHandler {


	private static final String TARGET_DEVICE_HISTOGRAM_FEATURE_NAME = "normalized_dst_machine_histogram";
	private static final UserActivityType ACTIVITY = UserActivityType.TARGET_DEVICE;

	@Autowired
	private UserActivityTargetDeviceConfigurationService userActivityTargetDeviceConfigurationService;

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		final String dataSourceLowerCase = dataSource.toLowerCase();
		UserActivityDataSourceConfiguration conf = userActivityTargetDeviceConfigurationService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
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
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	@Override
	Function<Integer, Integer> valueReducer() {
		return (newValue) -> 1;
	};

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
		return UserActivityTargetDeviceDocument.COLLECTION_NAME;
	}

	@Override
	protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
		return new ArrayList<>(Arrays.asList(TARGET_DEVICE_HISTOGRAM_FEATURE_NAME));
	}

	@Override
	public UserActivityType getActivity() {
		return ACTIVITY;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return userActivityTargetDeviceConfigurationService;
	}
}
