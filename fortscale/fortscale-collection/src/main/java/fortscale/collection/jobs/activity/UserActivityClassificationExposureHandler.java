package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityClassificationExposureService;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.activities.UserActivityClassificationExposureDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivityTopApplicationsDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
@Component
public class UserActivityClassificationExposureHandler extends UserActivityBaseHandler {

    private static final UserActivityType ACTIVITY = UserActivityType.CLASSIFICATION_EXPOSURE;

    public static final String CLASSIFICATION_EXPOSURE_HISTOGRAM_FEATURE_NAME = "classification_exposure_histogram";
    public static final String EVENT_COUNTER_HISTOGRAM_FEATURE_NAME = "event_counter";
    public static final String WAS_CLASSIFIED_HISTOGRAM_FEATURE_NAME = "classified_files_counter";

    @Autowired
    private UserActivityClassificationExposureService userActivityClassificationExposureService;

    @Override
    protected String getCollectionName() {
        return UserActivityTopApplicationsDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(CLASSIFICATION_EXPOSURE_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityClassificationExposureService;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityClassificationExposureService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityTopApplicationsDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
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
                case EVENT_COUNTER_HISTOGRAM_FEATURE_NAME:
                    histogram.add(UserActivityClassificationExposureDocument.FIELD_NAME_HISTOGRAM_EVENT_COUNTER,
                            convertAggregatedFeatureValueToDouble(((AggrFeatureValue) featureValue).getValue()));
                    break;
                case WAS_CLASSIFIED_HISTOGRAM_FEATURE_NAME:
                    histogram.add(UserActivityClassificationExposureDocument.FIELD_NAME_HISTOGRAM_WAS_CLASSIFIED,
                            convertAggregatedFeatureValueToDouble(((AggrFeatureValue) featureValue).getValue()));
                    break;
                default:
                    String errorMessage = String.format("Can't convert object %s to histogram. value is invalid: %s", objectToConvert, ((AggrFeatureValue) featureValue).getValue());
                    logger.error(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        } else {
            String errorMessage = String.format("Can't convert object %s object of class %s to histogram", objectToConvert, objectToConvert.getClass());
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        return histogram;
    }
}
