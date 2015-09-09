package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.services.historicaldata.populators.SupportingInformationDataPopulator;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
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
    public SupportingInformationData getEvidenceSupportingInformationData(Evidence evidence, String contextType, String contextValue, String featureName, Integer timePeriodInDays, String aggregationFunction) {
        EvidenceType evidenceType = evidence.getEvidenceType();
        List<String> dataEntities = evidence.getDataEntitiesIds();

        long evidenceEndTime = TimestampUtils.convertToMilliSeconds(evidence.getEndDate());

        logger.info("Going to calculate Evidence Supporting Information. Evidence Type = {} # Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", evidenceType, contextType, contextValue, dataEntities.get(0), featureName, TimeUtils.getFormattedTime(evidenceEndTime), aggregationFunction, timePeriodInDays);

        SupportingInformationDataPopulator supportingInformationPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator(evidenceType, contextType, dataEntities.get(0), featureName, aggregationFunction);

        long startTime = System.nanoTime();

        SupportingInformationData supportingInformationData = supportingInformationPopulator.createSupportingInformationData(evidence, contextValue, evidenceEndTime, timePeriodInDays);

        long elapsedTime = System.nanoTime() - startTime;

        logger.info("Retrieved Supporting Information Data in {} milliseconds. Returned data : {}", TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS), supportingInformationData.toString());

        return supportingInformationData;
    }
}
