package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;

/**
 * @author gils
 * Date: 05/08/2015
 */
public interface SupportingInformationDataPopulator {
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue);
}
