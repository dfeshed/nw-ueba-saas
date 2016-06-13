package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.logging.Logger;

import java.util.List;
import java.util.Map;

public class UserActivityWorkingHoursHandler extends UserActivityBaseHandler {
	@Override
	protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
		return null;
	}

	@Override
	protected String getCollectionName() {
		return null;
	}

	@Override
	protected List<String> getHistogramFeatureNames() {
		return null;
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

	@Override
	protected String getActivityName() {
		return null;
	}

	@Override
	protected UserActivityConfigurationService getUserActivityConfigurationService() {
		return null;
	}

	@Override
	protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
		return null;
	}

	@Override
	protected List<Class> getRelevantDocumentClasses() {
		return null;
	}

	@Override
	protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Integer> additionalActivityHistogram) {

	}
}
