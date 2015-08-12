package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;

/**
 * Interface for supporting information data population
 *
 * @author gils
 * Date: 05/08/2015
 */
public interface SupportingInformationDataPopulator {
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue);
}
