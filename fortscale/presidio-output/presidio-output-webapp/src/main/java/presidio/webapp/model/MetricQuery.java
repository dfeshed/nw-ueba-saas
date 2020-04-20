package presidio.webapp.model;

import java.util.List;

public class MetricQuery {

    List<String> metricNames;

    public MetricQuery() {
    }

    public MetricQuery(List<String> metricNames) {
        this.metricNames = metricNames;
    }

    public List<String> getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(List<String> metricNames) {
        this.metricNames = metricNames;
    }
}
