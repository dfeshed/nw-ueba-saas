package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;

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
     * @param contextValue the context value
     * @param evidenceEndTime evidence creation time
     * @param timePeriodInDays time period in days
     * @param anomalyValue anomaly value
     *
     * @return Supporting information data with anomaly value indication
     */
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue);

    /**
     * Populates the supporting information data based on the context value, evidence time (with no anomaly value indication)
     *
     * @param contextValue the context value
     * @param evidenceEndTime evidence creation time
     * @param timePeriodInDays time period in days
     *
     * @return Supporting information data (no anomaly value indication)
     */
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays);
}
