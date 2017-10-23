package presidio.monitoring.aspect.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import presidio.monitoring.elastic.records.PresidioMetric;
import presidio.monitoring.elastic.records.PresidioMetricWithLogicTime;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class PresidioCustomMetrics implements PublicMetrics {

    private Collection<PresidioMetric> reportEveryTimeMetrics;

    public static Collection<PresidioMetric> reportOnlyOnceMetrics;

    public PresidioCustomMetrics() {
        reportEveryTimeMetrics = new LinkedHashSet<>();
        reportOnlyOnceMetrics = new LinkedHashSet<>();
    }
/*
    public static void addInMethodMetric(String metricName, long metricValue, Set tags, String unit) {
        java.util.Iterator<PresidioMetric> itr = reportOnlyOnceMetrics.iterator();
        while (itr.hasNext()) {
            PresidioMetric metric = itr.next();
            if (metric.getName().equals(metricName)) {
                metric.setValue(metricValue + metric.getValue());
                return;
            }
        }
        reportOnlyOnceMetrics.add(new PresidioMetric(metricName, metricValue, tags, unit));
    }
*/

    public void addMetric(String metricName, long metricValue, Set tags, String unit, Date logicTime) {
        java.util.Iterator<PresidioMetric> itr = reportEveryTimeMetrics.iterator();
        while (itr.hasNext()) {
            PresidioMetric metric = itr.next();
            if (metric.getName().equals(metricName)) {
                metric.setValue(metricValue + metric.getValue());
                return;
            }
        }
        reportEveryTimeMetrics.add(createMetric(metricName, metricValue, tags, unit, logicTime));
    }

    public void addMetricReportOnce(String metricName, long metricValue, Set tags, String unit, Date logicTime) {
        java.util.Iterator<PresidioMetric> itr = reportOnlyOnceMetrics.iterator();
        while (itr.hasNext()) {
            PresidioMetric metric = itr.next();
            if (metric.getName().equals(metricName)) {
                metric.setValue(metricValue + metric.getValue());
                return;
            }
        }
        reportOnlyOnceMetrics.add(createMetric(metricName, metricValue, tags, unit, logicTime));
    }


    private PresidioMetric createMetric(String metricName, long metricValue, Set<String> tags, String unit, Date logicTime) {
        if (ObjectUtils.isEmpty(logicTime)) {
            return new PresidioMetric(metricName, metricValue, tags, unit);
        } else {
            return new PresidioMetricWithLogicTime(metricName, metricValue, tags, unit, logicTime);
        }
    }

    @Override
    public Collection<Metric<?>> metrics() {
        return null;
    }

    public Collection<PresidioMetric> applicationMetrics(boolean isFlush) {
        if (!isFlush) {
            return reportEveryTimeMetrics;
        }
        reportEveryTimeMetrics.addAll(reportOnlyOnceMetrics);
        return reportEveryTimeMetrics;
    }

}
