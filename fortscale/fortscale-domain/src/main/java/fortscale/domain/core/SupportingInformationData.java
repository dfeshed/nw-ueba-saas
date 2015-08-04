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

    private Map<Object, Double> histogram = new HashMap<>();

    public Map<Object, Double> getHistogram() {
        return histogram;
    }

    public void setHistogram(Map<Object, Double> historgam) {
        this.histogram = historgam;
    }
}
