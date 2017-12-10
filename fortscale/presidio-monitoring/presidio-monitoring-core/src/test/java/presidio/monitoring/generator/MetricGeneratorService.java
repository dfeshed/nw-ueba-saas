package presidio.monitoring.generator;


import presidio.monitoring.records.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MetricGeneratorService {

    public List<MetricDocument> generateMetrics(long numberOfMetrics, Instant fromDate, Instant toDate, List values, Metric metric) {
        List<MetricDocument> metrics = new LinkedList<>();
        long timeBetweenMetrics = (toDate.getEpochSecond() - fromDate.getEpochSecond()) / numberOfMetrics;
        Date timeStemp = new Date();
        for (int i = 0; i < numberOfMetrics; i++) {
            MetricDocument MetricDocument;
            Date time = Date.from(fromDate.plusMillis(timeBetweenMetrics * i));
            MetricDocument = new MetricDocument(metric.getName(), valuesMapGenerator(values), timeStemp, metric.getTags(), time);
            metrics.add(MetricDocument);
        }
        return metrics;
    }

    private Map<MetricEnums.MetricValues, Number> valuesMapGenerator(List<Number> values) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();

        Iterator itr = MetricEnums.MetricValues.collectionOfMetricValues().iterator();
        int number = new Random().nextInt(values.size());
        if (number == 0) {
            map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, 3);
        }
        for (int i = 0; i < number; i++) {
            map.put((MetricEnums.MetricValues) itr.next(), values.get(i));
        }
        return map;
    }
}
