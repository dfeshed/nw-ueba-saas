package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service implementation to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationServiceImpl implements SupportingInformationService {

    private static Logger logger = Logger.getLogger(SupportingInformationServiceImpl.class);

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private FeatureBucketsStore featureBucketsStore;

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, String dataEntity, String featureName,
                                                                          String anomalyType, String anomalyValue, long evidenceEndTime, int timePeriodInDays, String aggregationFunction) {
        logger.info("Going to calculate Evidence Supporting Information. Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", contextType, contextValue, dataEntity, featureName, TimeUtils.getFormattedTime(evidenceEndTime), aggregationFunction, timePeriodInDays);

        SupportingInformationDataPopulator supportingInformationPopulator = SupportingInformationPopulatorFactory.createSupportingInformationPopulator(contextType, dataEntity, featureName, aggregationFunction, bucketConfigurationService, featureBucketsStore);

        SupportingInformationData supportingInformationData = supportingInformationPopulator.createSupportingInformationData(contextValue, evidenceEndTime, timePeriodInDays, anomalyValue);

        return supportingInformationData;
    }
}
