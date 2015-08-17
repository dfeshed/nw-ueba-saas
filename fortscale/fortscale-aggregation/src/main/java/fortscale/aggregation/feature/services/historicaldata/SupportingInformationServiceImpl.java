package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
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

    private static final String VPN_GEO_HOPPING_ANOMALY_TYPE = "vpn_geo_hopping";

    @Autowired
    private SupportingInformationPopulatorFactory supportingInformationPopulatorFactory;

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(Evidence evidence, String contextType, String contextValue, String featureName,
                                                                           long evidenceEndTime, int timePeriodInDays, String aggregationFunction) {
        EvidenceType evidenceType = evidence.getEvidenceType();
        List<String> dataEntities = evidence.getDataEntitiesIds();

        // MOCK
        evidenceType = EvidenceType.AnomalyAggregatedEvent;
        // END MOCK

        SupportingInformationDataPopulator supportingInformationPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator(evidenceType, contextType, dataEntities.get(0), featureName, aggregationFunction);

        boolean isAnomalyIndicationRequired = isAnomalyIndicationRequired(evidence);

        logger.info("Going to calculate Evidence Supporting Information. Evidence Type = {} # Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", evidenceType, contextType, contextValue, dataEntities.get(0), featureName, TimeUtils.getFormattedTime(evidenceEndTime), aggregationFunction, timePeriodInDays);

        long startTime = System.nanoTime();

        SupportingInformationData supportingInformationData = supportingInformationPopulator.createSupportingInformationData(evidence, contextValue, evidenceEndTime, timePeriodInDays, isAnomalyIndicationRequired);

        long elapsedTime = System.nanoTime() - startTime;

        logger.info("Calculated Supporting Information Data in {} milliseconds. Returned data : {}", TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS), supportingInformationData.toString());

        return supportingInformationData;
    }

    private boolean isAnomalyIndicationRequired(Evidence evidence) {
        // TODO should be defined in enum or static map. currently the only exception is the vpn geo hopping type
        return !VPN_GEO_HOPPING_ANOMALY_TYPE.equals(evidence.getAnomalyTypeFieldName());
    }
}
