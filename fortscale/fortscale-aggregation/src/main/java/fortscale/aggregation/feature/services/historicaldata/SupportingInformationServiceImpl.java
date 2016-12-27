package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.services.historicaldata.populators.SupportingInformationDataPopulator;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.rest.HistoricalDataRestFilter;
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
    public SupportingInformationData getEvidenceSupportingInformationData(Evidence evidence, HistoricalDataRestFilter historicalDataRestFilter) {
        EvidenceType evidenceType = evidence.getEvidenceType();
        List<String> dataEntities = evidence.getDataEntitiesIds();

        long evidenceEndTime = TimestampUtils.convertToMilliSeconds(evidence.getEndDate());

        logger.info("Going to calculate Evidence Supporting Information. Evidence Type = {} # Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", evidenceType, historicalDataRestFilter.getContextType(), historicalDataRestFilter.getContextValue(), dataEntities.get(0), historicalDataRestFilter.getFeature(), TimeUtils.getFormattedTime(evidenceEndTime), historicalDataRestFilter.getFunction(), historicalDataRestFilter.getTimeRange());

        SupportingInformationDataPopulator supportingInformationPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator(evidenceType, historicalDataRestFilter.getContextType(), dataEntities.get(0), historicalDataRestFilter.getFeature(), historicalDataRestFilter.getFunction());

        long startTime = System.nanoTime();

        SupportingInformationData supportingInformationData = supportingInformationPopulator.createSupportingInformationData(evidence, historicalDataRestFilter.getContextValue(), evidenceEndTime, historicalDataRestFilter.getTimeRange());

        long elapsedTime = System.nanoTime() - startTime;

        logger.info("Retrieved Supporting Information Data in {} milliseconds. Returned data : {}", TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS), supportingInformationData.toString());

        return supportingInformationData;
    }
}
