package presidio.monitoring.generator;


import presidio.monitoring.records.PresidioMetric;
import presidio.monitoring.records.PresidioMetricWithLogicTime;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MetricGeneratorService {

    public List<PresidioMetric> generateMetrics(long numberOfMetrics, Instant fromDate, Instant toDate, String metricName, Set values, String unit, Set tags, boolean reportOnce) {
        List<PresidioMetric> metrics = new LinkedList<>();
        long timeBetweenMetrics = (toDate.getEpochSecond() - fromDate.getEpochSecond()) / numberOfMetrics;
        for (int i = 0; i < numberOfMetrics; i++) {
            PresidioMetric metric;
            Date time = Date.from(fromDate.plusMillis(timeBetweenMetrics * i));
            if (reportOnce) {
                metric = new PresidioMetricWithLogicTime(metricName, getValue(values), tags, unit, time);
            } else {
                metric = new PresidioMetric(metricName, getValue(values), tags, unit);
            }
            metric.setTimestamp(time);
            metrics.add(metric);
        }
        return metrics;
    }

    private long getValue(Set values) {
        return (long) values.toArray()[new Random().nextInt(values.size())];
    }
}
