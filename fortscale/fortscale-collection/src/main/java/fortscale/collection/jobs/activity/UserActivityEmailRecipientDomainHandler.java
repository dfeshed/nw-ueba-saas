package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivityEmailRecipientDomainService;
import fortscale.domain.core.activities.UserActivityEmailRecipientDomainDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
@Component
public class UserActivityEmailRecipientDomainHandler extends UserActivityBaseHandler {

    private static final UserActivityType EMAIL_RECIPIENT_DOMAIN = UserActivityType.EMAIL_RECIPIENT_DOMAIN;
    public static final String EXECUTING_EMAIL_RECIPIENT_DOMAIN_HISTOGRAM_FEATURE_NAME = "executing_email_recipient_domain_histogram";

    @Autowired
    private UserActivityEmailRecipientDomainService userActivityEmailRecipientDomainService;

    @Override
    protected String getCollectionName() {
        return UserActivityEmailRecipientDomainDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(EXECUTING_EMAIL_RECIPIENT_DOMAIN_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return EMAIL_RECIPIENT_DOMAIN;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityEmailRecipientDomainService;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityEmailRecipientDomainService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityEmailRecipientDomainDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }
}
