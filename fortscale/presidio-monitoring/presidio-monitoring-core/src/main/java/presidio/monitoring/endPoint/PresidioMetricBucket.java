package presidio.monitoring.endPoint;

import org.elasticsearch.common.collect.Tuple;
import org.springframework.util.ObjectUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricConventionApplyer;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PresidioMetricBucket {

    private Map<Tuple<String, Instant>, Metric> applicationMetrics;
    private PresidioSystemMetricsFactory presidioSystemMetricsFactory;
    private MetricConventionApplyer metricConventionApplyer;

    public PresidioMetricBucket(PresidioSystemMetricsFactory presidioSystemMetricsFactory, MetricConventionApplyer metricConventionApplyer) {
        this.presidioSystemMetricsFactory = presidioSystemMetricsFactory;
        this.metricConventionApplyer = metricConventionApplyer;
        this.applicationMetrics = new HashMap<>();
    }

    public void addMetric(Metric metric) {
        metricConventionApplyer.apply(metric);
        if (isMetricExist(metric)) {
            accumulateMetricValues(metric, applicationMetrics.get(Tuple.tuple(metric.getName(), metric.getLogicTime())).getValue());
        }
        applicationMetrics.put(Tuple.tuple(metric.getName(), metric.getLogicTime()), metric);
    }

    private void accumulateMetricValues(Metric metric, Map<MetricEnums.MetricValues, Number> value) {
        Map<MetricEnums.MetricValues, Number> metricValues = metric.getValue();
        for (Map.Entry<MetricEnums.MetricValues, Number> entry : metricValues.entrySet()) {
            if (!ObjectUtils.isEmpty(value.get(entry.getKey()))) {
                entry.setValue(operatorAddForNumber(value.get(entry.getKey()), entry.getValue()));
            }
        }
        for (Map.Entry<MetricEnums.MetricValues, Number> entry : value.entrySet()) {
            if (!metricValues.containsKey(entry.getKey())) {
                metricValues.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean isMetricExist(Metric metric) {
        boolean isExist = false;
        if (!ObjectUtils.isEmpty(applicationMetrics.get(Tuple.tuple(metric.getName(), metric.getLogicTime())))) {
            Instant logicalTime = applicationMetrics.get(Tuple.tuple(metric.getName(), metric.getLogicTime())).getLogicTime();
            if (!ObjectUtils.isEmpty(logicalTime) && !ObjectUtils.isEmpty(metric.getLogicTime()) && logicalTime.equals(metric.getLogicTime())) {
                isExist = true;
            }
            if (ObjectUtils.isEmpty(logicalTime) && ObjectUtils.isEmpty(metric.getLogicTime())) {
                isExist = true;
            }
        }
        return isExist;
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
        allMetrics.addAll(getSystemMetrics());
        applicationMetrics = new HashMap<>();
        return allMetrics;
    }

    public List<MetricDocument> getApplicationMetricsAndResetApplicationMetrics() {
        List<MetricDocument> allMetrics = new LinkedList<>();
        applicationMetrics.forEach((s, metric) -> {
            allMetrics.add(buildPresidioMetric(metric));
        });
        applicationMetrics = new HashMap<>();
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
        return new MetricDocument(metric.getName(),
                metric.getValue(),
                Date.from(metric.getTime()),
                metric.getTags(),
                metric.getLogicTime() != null ? Date.from(metric.getLogicTime()) : null);
    }
}
