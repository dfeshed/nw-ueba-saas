package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.historical.data.SupportingInformationKey;

import java.util.Map;


/**
 * Interface for supporting information data representation
 *
 * @author gils
 * Date: 03/09/2015
 */
public interface SupportingInformationData<T> {
    Map<SupportingInformationKey, T> getData();
    SupportingInformationKey getAnomalyValue();
    SupportingInformationTimeGranularity getTimeGranularity();
    @SuppressWarnings("rawtypes")
	Map<SupportingInformationKey, Map> getAdditionalInformation();
}
