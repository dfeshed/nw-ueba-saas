package presidio.monitoring.generator;


import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.records.MetricWithLogicTimeDocument;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MetricGeneratorService {

    public List<MetricDocument> generateMetrics(long numberOfMetrics, Instant fromDate, Instant toDate, String metricName, List values, String unit, Set tags, boolean reportOnce) {
        List<MetricDocument> metrics = new LinkedList<>();
        long timeBetweenMetrics = (toDate.getEpochSecond() - fromDate.getEpochSecond()) / numberOfMetrics;
        for (int i = 0; i < numberOfMetrics; i++) {
            MetricDocument metric;
            Date time = Date.from(fromDate.plusMillis(timeBetweenMetrics * i));
            if (reportOnce) {
                metric = new MetricWithLogicTimeDocument(metricName, getValue(values), tags, unit, time);
            } else {
                metric = new MetricDocument(metricName, getValue(values), tags, unit);
            }
            metric.setTimestamp(time);
            metrics.add(metric);
        }
        return metrics;
    }

    private long getValue(List values) {
        return Long.valueOf((Integer) values.get(new Random().nextInt(values.size()))).longValue();
    }
}
