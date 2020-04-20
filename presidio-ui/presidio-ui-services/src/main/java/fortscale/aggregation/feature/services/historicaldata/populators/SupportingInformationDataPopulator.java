package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.domain.core.Evidence;

/**
 * Interface for supporting information data population. Populator should provide the complete
 * supporting information data based on the context value, evidence time and anomaly value.
 *
 * @author gils
 * Date: 05/08/2015
 */
public interface SupportingInformationDataPopulator {
    /**
     * Populates the supporting information data based on the context value, evidence time and anomaly value.
     *
     * @param evidence the evidence
     * @param contextValue the context value
     * @param evidenceEndTime evidence creation time
     * @param timePeriodInDays time period in days
     *
     * @return Supporting information data with/without anomaly value indication
     */
    SupportingInformationData createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, Integer timePeriodInDays);
}
