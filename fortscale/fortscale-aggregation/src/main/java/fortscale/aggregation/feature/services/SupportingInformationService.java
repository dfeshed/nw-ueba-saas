package fortscale.aggregation.feature.services;

import fortscale.domain.core.SupportingInformationData;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Service to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public interface SupportingInformationService {
    /**
     *
     * @param entityType the entity type (user, machine etc.)
     * @param entityName the entity name (e.g. mike@cnn.com)
     * @param dataSource the data source (ssh, kerberos, etc.)
     * @param feature the related feature
     * @param evidenceTime the evidence time in milliseconds
     *
     * @return supporting information representation
     */
    SupportingInformationData getEvidenceSupportingInformationData(String entityType, String entityName, String dataSource, String feature, Long evidenceTime);
}