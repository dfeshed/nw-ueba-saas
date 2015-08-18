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
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue);
}
