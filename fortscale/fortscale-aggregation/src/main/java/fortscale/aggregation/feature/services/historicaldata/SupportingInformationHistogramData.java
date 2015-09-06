package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.historical.data.SupportingInformationKey;

import java.util.Map;

/**
 * Representation of Supporting Information data for histograms
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationHistogramData extends SupportingInformationGenericData<Double> {

    public SupportingInformationHistogramData(Map<SupportingInformationKey, Double> dataMap, SupportingInformationKey anomalyValue) {
        super(dataMap, anomalyValue);
    }

    public SupportingInformationHistogramData(Map<SupportingInformationKey, Double> dataMap) {
        super(dataMap);
    }

    @Override
    public String toString() {
        return "SupportingInformationHistogramData{" +
                "histogram=" + dataMap +
                ", additionalInformation=" + additionalInformation +
                ", anomalyValue=" + anomalyValue +
                ", timeGranularity=" + timeGranularity +
                '}';
    }
}