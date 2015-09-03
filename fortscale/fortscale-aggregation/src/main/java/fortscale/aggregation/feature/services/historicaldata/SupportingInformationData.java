package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.historical.data.SupportingInformationKey;

import java.util.Map;


/**
 * @author gils
 * Date: 03/09/2015
 */
public interface SupportingInformationData<VAL_TYPE> {
    Map<SupportingInformationKey, VAL_TYPE> getData();
    SupportingInformationKey getAnomalyValue();
    SupportingInformationTimeGranularity getTimeGranularity();
    Map<SupportingInformationKey, Map> getAdditionalInformation();
}
