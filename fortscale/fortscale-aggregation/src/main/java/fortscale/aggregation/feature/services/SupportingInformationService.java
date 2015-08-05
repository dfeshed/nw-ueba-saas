package fortscale.aggregation.feature.services;

import fortscale.domain.core.SupportingInformationData;

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
     * @param dataEntity the data source (ssh, kerberos, etc.)
     * @param feature the related feature
     * @param aggregationEventEndTime the evidence time in milliseconds
     *
     * @return supporting information representation
     */
    SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, String dataEntity, String feature, Long aggregationEventEndTime);
}