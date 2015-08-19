package fortscale.domain.core;

import fortscale.domain.histogram.HistogramKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of Supporting Information data.
 * Data must contain the histogram mapping (key-value) and optionally the anomaly value
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationData {

    private Map<HistogramKey, Double> histogram = new HashMap<>();

    private Map<HistogramKey, Map> additionalInformation = new HashMap();

    private HistogramKey anomalyValue;

    public SupportingInformationData(Map<HistogramKey, Double> histogram, HistogramKey anomalyValue) {
        this.histogram = histogram;
        this.anomalyValue = anomalyValue;
    }

    public SupportingInformationData(Map<HistogramKey, Double> histogram) {
        this.histogram = histogram;
    }

    public Map<HistogramKey, Double> getHistogram() {
        return Collections.unmodifiableMap(histogram);
    }

    public HistogramKey getAnomalyValue() {
        return anomalyValue;
    }

    @Override
    public String toString() {
        return "SupportingInformationData{" +
                "histogram=" + histogram +
                ", anomalyValue=" + anomalyValue +
                ", additionalInformation=" + additionalInformation +
                '}';
    }

    public Map<HistogramKey, Map> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<HistogramKey, Map> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}