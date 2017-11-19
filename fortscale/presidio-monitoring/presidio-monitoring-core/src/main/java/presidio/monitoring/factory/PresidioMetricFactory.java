package presidio.monitoring.factory;

import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.records.Metric;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PresidioMetricFactory {

    private String applicationName;

    public PresidioMetricFactory(String applicationName) {
        this.applicationName = applicationName;
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String unit, Instant logicTime, boolean reportOnce) {
        tags.add(applicationName);
        return createMetric(metricName, value, tags, unit, Date.from(logicTime), reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, Set<String> tags, String unit, Instant logicTime, boolean reportOnce) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SUM, value);
        return creatingPresidioMetric(metricName, map, tags, unit, logicTime, reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String unit, Instant logicTime) {
        tags.add(applicationName);
        return createMetric(metricName, value, tags, unit, Date.from(logicTime), false);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, Set<String> tags, String unit, Instant logicTime) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SUM, value);
        return createMetric(metricName, map, tags, unit, Date.from(logicTime), false);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String unit, boolean reportOnce) {
        tags.add(applicationName);
        return createMetric(metricName, value, tags, unit, null, reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String unit) {
        tags.add(applicationName);
        return createMetric(metricName, value, tags, unit, null, false);

    }

    public Metric creatingPresidioMetric(String metricName, Number value, Set<String> tags, String unit) {
        Metric metric = creatingPresidioMetric(metricName, value, unit);
        metric.setTags(tags);
        return metric;
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, String unit) {
        Set<String> tags = new HashSet<>();
        tags.add(applicationName);
        return createMetric(metricName, value, tags, unit, null, false);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, String unit) {
        Set<String> tags = new HashSet<>();
        tags.add(applicationName);
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SUM, value);
        return createMetric(metricName, map, tags, unit, null, false);
    }

    private Metric createMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Set<String> tags, String unit, Date logicTime, boolean reportOnce) {
        return new Metric(metricName, value, logicTime, tags, unit, reportOnce);
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
