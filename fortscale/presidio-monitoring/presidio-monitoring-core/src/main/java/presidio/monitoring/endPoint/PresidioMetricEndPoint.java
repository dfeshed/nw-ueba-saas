package presidio.monitoring.endPoint;

import org.springframework.util.ObjectUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.records.PresidioMetric;
import presidio.monitoring.records.PresidioMetricWithLogicTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by maors on 10/29/2017.
 */
public class PresidioMetricEndPoint {

    private Map<String, Metric> metrics;
    private PresidioSystemMetrics presidioSystemMetrics;

    public PresidioMetricEndPoint(PresidioSystemMetrics presidioSystemMetrics) {
        this.presidioSystemMetrics = presidioSystemMetrics;
        this.metrics = new HashMap<>();
    }

    public void addMetric(Metric metric) {
        if (ObjectUtils.isEmpty(metrics.get(metric.getName()))) {
            metrics.put(metric.getName(), metric);
        } else {
            long value = metrics.get(metric.getName()).getValue();
            metrics.get(metric.getName()).setValue(metric.getValue() + value);
        }
    }

    public List<PresidioMetric> getAllMetrics(boolean lastExport) {
        List<PresidioMetric> allMetrics = new LinkedList<>();
        metrics.forEach((s, metric) -> {
            if (metric.isReportOneTime()) {
                if (lastExport)
                    allMetrics.add(buildPresidioMetric(metric));
            } else {
                allMetrics.add(buildPresidioMetric(metric));
            }
        });
        allMetrics.addAll(buildPresidioMetricsFromSystemMetrics(presidioSystemMetrics.metrics()));
        return allMetrics;
    }

    private PresidioMetric buildPresidioMetric(Metric metric) {
        if (ObjectUtils.isEmpty(metric.getLogicTime())) {
            return new PresidioMetric(metric.getName(), metric.getValue(), metric.getTime(), metric.getTags(), metric.getUnit());
        } else {
            return new PresidioMetricWithLogicTime(metric.getName(), metric.getValue(), metric.getTime(), metric.getTags(), metric.getUnit(), metric.getLogicTime());

        }
    }

    private List<PresidioMetric> buildPresidioMetricsFromSystemMetrics(List<Metric> systemMetrics) {
        List<PresidioMetric> allSystemMetrics = new LinkedList<>();
        systemMetrics.forEach(metric -> {
            allSystemMetrics.add(buildPresidioMetric(metric));
        });
        return allSystemMetrics;
    }

}
