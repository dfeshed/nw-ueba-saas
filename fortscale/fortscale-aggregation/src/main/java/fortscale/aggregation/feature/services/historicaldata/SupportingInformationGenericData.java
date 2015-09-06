package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.historical.data.SupportingInformationKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic implementation of supporting information data
 *
 * @author gils
 * Date: 03/09/2015
 */
public class SupportingInformationGenericData<VAL_TYPE> implements SupportingInformationData{
    protected Map<SupportingInformationKey, VAL_TYPE> dataMap = new HashMap<>();

    protected Map<SupportingInformationKey, Map> additionalInformation = new HashMap<>();

    protected SupportingInformationKey anomalyValue;

    protected SupportingInformationTimeGranularity timeGranularity;

    public SupportingInformationGenericData(Map<SupportingInformationKey, VAL_TYPE> dataMap, SupportingInformationKey anomalyValue) {
        this.dataMap = dataMap;
        this.anomalyValue = anomalyValue;
    }

    public SupportingInformationGenericData(Map<SupportingInformationKey, VAL_TYPE> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public Map<SupportingInformationKey, VAL_TYPE> getData() {
        return dataMap;
    }

    public SupportingInformationKey getAnomalyValue() {
        return anomalyValue;
    }

    public Map<SupportingInformationKey, Map> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<SupportingInformationKey, Map> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "SupportingInformationHistogramData{" +
                "dataMap=" + dataMap +
                ", additionalInformation=" + additionalInformation +
                ", anomalyValue=" + anomalyValue +
                ", timeGranularity=" + timeGranularity +
                '}';
    }

    public SupportingInformationTimeGranularity getTimeGranularity() {
        return timeGranularity;
    }

    public void setTimeGranularity(SupportingInformationTimeGranularity timeGranularity) {
        this.timeGranularity = timeGranularity;
    }
}
