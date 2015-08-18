package fortscale.domain.core;

import fortscale.domain.histogram.HistogramKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of the Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationData {

    private Map<HistogramKey, Double> histogram = new HashMap<>();

    private HistogramKey anomalyValue;

    public SupportingInformationData(Map<HistogramKey, Double> histogram, HistogramKey anomalyValue) {
        this.histogram = histogram;
        this.anomalyValue = anomalyValue;
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
                '}';
    }
}
