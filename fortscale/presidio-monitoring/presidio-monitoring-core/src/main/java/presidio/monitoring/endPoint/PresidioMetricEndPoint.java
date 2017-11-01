package presidio.monitoring.endPoint;

import org.springframework.util.ObjectUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.records.MetricWithLogicTimeDocument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PresidioMetricEndPoint {

    private Map<String, Metric> applicationMetrics;
    private PresidioSystemMetricsFactory presidioSystemMetricsFactory;

    public PresidioMetricEndPoint(PresidioSystemMetricsFactory presidioSystemMetricsFactory) {
        this.presidioSystemMetricsFactory = presidioSystemMetricsFactory;
        this.applicationMetrics = new HashMap<>();
    }

    public void addMetric(Metric metric) {
        if (ObjectUtils.isEmpty(applicationMetrics.get(metric.getName()))) {
            applicationMetrics.put(metric.getName(), metric);
        } else {
            long value = applicationMetrics.get(metric.getName()).getValue();
            applicationMetrics.get(metric.getName()).setValue(metric.getValue() + value);
        }
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

    private MetricDocument buildPresidioMetric(Metric metric) {
        if (ObjectUtils.isEmpty(metric.getLogicTime())) {
            return new MetricDocument(metric.getName(), metric.getValue(), metric.getTime(), metric.getTags(), metric.getUnit());
        } else {
            return new MetricWithLogicTimeDocument(metric.getName(), metric.getValue(), metric.getTime(), metric.getTags(), metric.getUnit(), metric.getLogicTime());

        }
    }

    private List<MetricDocument> buildPresidioMetricsFromSystemMetrics(List<Metric> systemMetrics) {
        List<MetricDocument> allSystemMetrics = new LinkedList<>();
        systemMetrics.forEach(metric -> {
            allSystemMetrics.add(buildPresidioMetric(metric));
        });
        return allSystemMetrics;
    }

}
