package presidio.monitoring.aspect.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class PresidioCustomMetrics implements PublicMetrics {

    private Collection<PresidioMetric> reportEveryTimeMetrics;

    public static Collection<PresidioMetric> reportOnlyOnceMetrics;

    public PresidioCustomMetrics() {
        reportEveryTimeMetrics = new LinkedHashSet<>();
        reportOnlyOnceMetrics = new LinkedHashSet<>();
    }

    public void addMetric(PresidioMetric PresidioMetric) {
        java.util.Iterator<PresidioMetric> itr = reportEveryTimeMetrics.iterator();
        while (itr.hasNext()) {
            PresidioMetric metric = itr.next();
            if (metric.getName().equals(PresidioMetric.getName())) {
                metric.setValue(PresidioMetric.getValue() + metric.getValue());
                return;
            }
        }
        reportEveryTimeMetrics.add(PresidioMetric);
    }

    public void addMetricReportOnce(PresidioMetric PresidioMetric) {
        java.util.Iterator<PresidioMetric> itr = reportOnlyOnceMetrics.iterator();
        while (itr.hasNext()) {
            PresidioMetric metric = itr.next();
            if (metric.getName().equals(PresidioMetric.getName())) {
                metric.setValue(PresidioMetric.getValue() + metric.getValue());
                return;
            }
        }
        reportEveryTimeMetrics.add(PresidioMetric);
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
