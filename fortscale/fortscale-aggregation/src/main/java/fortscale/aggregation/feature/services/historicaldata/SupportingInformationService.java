package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.Evidence;
import fortscale.domain.rest.HistoricalDataRestFilter;

/**
 * Service to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public interface SupportingInformationService {
    /**
     *
     * @param evidence the evidence
     * @param historicalDataRequest the parameters which required to fetch the supporting infromation
     *
     * @return supporting information data representation
     */
    SupportingInformationData getEvidenceSupportingInformationData(Evidence evidence, HistoricalDataRestFilter historicalDataRequest);
}