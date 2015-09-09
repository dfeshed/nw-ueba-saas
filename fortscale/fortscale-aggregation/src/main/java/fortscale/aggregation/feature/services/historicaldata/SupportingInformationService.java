package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.Evidence;

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
     * @param contextType the context type (user, source machine, dest machine etc.)
     * @param contextValue the context value (e.g. mike@cnn.com)
     * @param featureName the related feature name
     * @param timePeriodInDays evidence end time in milliseconds (optional value)
     * @param aggregationFunction the aggregation function
     *
     * @return supporting information data representation
     */
    SupportingInformationData getEvidenceSupportingInformationData(Evidence evidence, String contextType, String contextValue, String featureName, Integer timePeriodInDays, String aggregationFunction);
}