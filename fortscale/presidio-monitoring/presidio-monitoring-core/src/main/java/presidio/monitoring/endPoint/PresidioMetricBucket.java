package presidio.monitoring.endPoint;

import org.springframework.util.ObjectUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PresidioMetricBucket {

    private String applicationName;
    private Map<String, Metric> applicationMetrics;
    private PresidioSystemMetricsFactory presidioSystemMetricsFactory;

    public PresidioMetricBucket(PresidioSystemMetricsFactory presidioSystemMetricsFactory, String applicationName) {
        this.applicationName = applicationName;
        this.presidioSystemMetricsFactory = presidioSystemMetricsFactory;
        this.applicationMetrics = new HashMap<>();
    }

    public void addMetric(Metric metric) {
        metric.addTag(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        if (!ObjectUtils.isEmpty(applicationMetrics.get(metric.getName()))) {
            aggregateMetricValues(metric, applicationMetrics.get(metric.getName()).getValue());
        }
        applicationMetrics.put(metric.getName(), metric);
    }

    private void aggregateMetricValues(Metric metric, Map<MetricEnums.MetricValues, Number> value) {
        Map<MetricEnums.MetricValues, Number> metricValues = metric.getValue();
        for (Map.Entry<MetricEnums.MetricValues, Number> entry : metricValues.entrySet()) {
            if (value.get(entry.getKey()) != null) {
                entry.setValue(operatorAddForNumber(value.get(entry.getKey()), entry.getValue()));
            }
        }
    }

    private Number operatorAddForNumber(Number number1, Number number2) {
        if (number1 instanceof Integer) {
            return number1.intValue() + number2.intValue();
        }
        if (number1 instanceof Double) {
            return number1.doubleValue() + number2.doubleValue();
        }
        return number1.longValue() + number2.longValue();
    }


    public List<MetricDocument> getAllMetrics(boolean lastExport) {
        List<MetricDocument> allMetrics = new LinkedList<>();
        applicationMetrics.forEach((s, metric) -> {
            if (metric.isReportOneTime()) {
                if (lastExport)
                    allMetrics.add(buildPresidioMetric(metric));
            } else {
                allMetrics.add(buildPresidioMetric(metric));
            }
        });
        allMetrics.addAll(buildPresidioMetricsFromSystemMetrics(presidioSystemMetricsFactory.metrics()));
        return allMetrics;
    }

    public List<MetricDocument> getApplicationMetrics() {
        List<MetricDocument> allMetrics = new LinkedList<>();
        applicationMetrics.forEach((s, metric) -> {
            if (!metric.isReportOneTime()) {
                allMetrics.add(buildPresidioMetric(metric));
            }
        });
        return allMetrics;
    }

    public List<MetricDocument> getSystemMetrics() {
        return buildPresidioMetricsFromSystemMetrics(presidioSystemMetricsFactory.metrics());
    }

    private List<MetricDocument> buildPresidioMetricsFromSystemMetrics(List<Metric> systemMetrics) {
        List<MetricDocument> allSystemMetrics = new LinkedList<>();
        systemMetrics.forEach(metric -> {
            allSystemMetrics.add(buildPresidioMetric(metric));
        });
        return allSystemMetrics;
    }

    private MetricDocument buildPresidioMetric(Metric metric) {
        return new MetricDocument(metric.getName(), metric.getValue(), metric.getTime(), metric.getTags(), metric.getLogicTime());
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
