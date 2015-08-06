package fortscale.domain.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of the Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationData {

    private Map<String, Double> histogram = new HashMap<>();

    public Map<String, Double> getHistogram() {
        return histogram;
    }

    public void setHistogram(Map<String, Double> historgam) {
        this.histogram = historgam;
    }
}
