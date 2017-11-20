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

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit, Instant logicTime, boolean reportOnce) {
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);

        return createMetric(metricName, value, tags, Date.from(logicTime), reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit, Instant logicTime, boolean reportOnce) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);

        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return creatingPresidioMetric(metricName, map, tags, unit, logicTime, reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit, Instant logicTime) {
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return createMetric(metricName, value, tags, Date.from(logicTime), false);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit, Instant logicTime) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return createMetric(metricName, map, tags, Date.from(logicTime), false);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit, boolean reportOnce) {
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return createMetric(metricName, value, tags, null, reportOnce);
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit) {
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return createMetric(metricName, value, tags, null, false);

    }

    public Metric creatingPresidioMetric(String metricName, Number value, Map<MetricEnums.MetricTagKeysEnum, String> tags, String unit) {
        Metric metric = creatingPresidioMetric(metricName, value, unit);
        metric.setTags(tags);
        return metric;
    }

    public Metric creatingPresidioMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, String unit) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);
        return createMetric(metricName, value, tags, null, false);
    }

    public Metric creatingPresidioMetric(String metricName, Number value, String unit) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, unit);

        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SUM, value);
        return createMetric(metricName, map, tags, null, false);
    }

    private Metric createMetric(String metricName, Map<MetricEnums.MetricValues, Number> value, Map<MetricEnums.MetricTagKeysEnum, String> tags, Date logicTime, boolean reportOnce) {
        return new Metric(metricName, value, logicTime, tags, reportOnce);
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
