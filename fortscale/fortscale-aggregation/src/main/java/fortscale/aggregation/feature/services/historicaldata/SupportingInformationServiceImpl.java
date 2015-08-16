package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service implementation to provide Supporting Information data based on historical data
 *
 * @author gils
 * Date: 29/07/2015
 */
@Service
public class SupportingInformationServiceImpl implements SupportingInformationService {

    private static Logger logger = Logger.getLogger(SupportingInformationServiceImpl.class);

    @Autowired
    private SupportingInformationPopulatorFactory supportingInformationPopulatorFactory;

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, List<String> dataEntities, String featureName,
                                                                           String anomalyValue, long evidenceEndTime, int timePeriodInDays, String aggregationFunction) {
        logger.info("Going to calculate Evidence Supporting Information. Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Anomaly value = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", contextType, contextValue, dataEntities.get(0), featureName, anomalyValue, TimeUtils.getFormattedTime(evidenceEndTime), aggregationFunction, timePeriodInDays);

        SupportingInformationDataPopulator supportingInformationPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator(contextType, dataEntities.get(0), featureName, aggregationFunction);

        SupportingInformationData supportingInformationData;

        long startTime = System.nanoTime();
        if (anomalyValue != null) {
            supportingInformationData = supportingInformationPopulator.createSupportingInformationData(contextValue, evidenceEndTime, timePeriodInDays, anomalyValue);
        }
        else {
            supportingInformationData = supportingInformationPopulator.createSupportingInformationData(contextValue, evidenceEndTime, timePeriodInDays);
        }

        long elapsedTime = System.nanoTime() - startTime;

        logger.info("Calculated Supporting Information Data in {} milliseconds. Returned data : {}", TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS), supportingInformationData.toString());

        return supportingInformationData;
    }

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, List<String> dataEntities, String featureName,
                                                                          long evidenceEndTime, int timePeriodInDays, String aggregationFunction) {
        return getEvidenceSupportingInformationData(contextType, contextValue, dataEntities, featureName, null, evidenceEndTime, timePeriodInDays, aggregationFunction);
    }
}
