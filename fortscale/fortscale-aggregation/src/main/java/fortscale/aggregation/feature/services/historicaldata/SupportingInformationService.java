package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;

import java.util.List;

/**
 * Service to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public interface SupportingInformationService {
    /**
     *
     * @param contextType the context type (user, source machine, dest machine etc.)
     * @param contextValue the context value (e.g. mike@cnn.com)
     * @param dataEntities the data entity (ssh, kerberos, etc.)
     * @param featureName the related feature name
     * @param anomalyType evidence end time in milliseconds
     * @param anomalyValue evidence end time in milliseconds
     * @param evidenceEndTime evidence end time in milliseconds
     * @param timePeriodInDays evidence end time in milliseconds
     * @param aggregationFunction the aggregation function
     *
     * @return supporting information data representation
     */
    SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, List<String> dataEntities, String featureName,
                                                                   String anomalyType, String anomalyValue, long evidenceEndTime, int timePeriodInDays, String aggregationFunction);
}