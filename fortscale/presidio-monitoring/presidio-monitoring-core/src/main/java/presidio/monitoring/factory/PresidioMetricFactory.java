package presidio.monitoring.factory;

import presidio.monitoring.records.Metric;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class PresidioMetricFactory {

    private String applicationName;

    public PresidioMetricFactory(String applicationName) {
        this.applicationName = applicationName;
    }

    public Metric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit, Instant logicTime, boolean reportOnce) {
        tags.add(applicationName);
        return createMetric(metricName, metricValue, tags, unit, Date.from(logicTime), reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit, Instant logicTime) {
        tags.add(applicationName);
        return createMetric(metricName, metricValue, tags, unit, Date.from(logicTime), false);
    }

    public Metric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit, boolean reportOnce) {
        tags.add(applicationName);
        return createMetric(metricName, metricValue, tags, unit, null, reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Number metricValue, Set<String> tags, String unit) {
        tags.add(applicationName);
        return createMetric(metricName, metricValue, tags, unit, null, false);

    }

    public Metric creatingPresidioMetric(String metricName, Number metricValue, String unit) {
        Set<String> tags = new HashSet<>();
        tags.add(applicationName);
        return createMetric(metricName, metricValue, tags, unit, null, false);

    }

    private Metric createMetric(String metricName, Number metricValue, Set<String> tags, String unit, Date logicTime, boolean reportOnce) {
        return new Metric(metricName, metricValue.longValue(), logicTime, tags, unit, reportOnce);
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
