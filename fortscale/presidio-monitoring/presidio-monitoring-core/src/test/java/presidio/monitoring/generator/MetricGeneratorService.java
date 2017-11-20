package presidio.monitoring.generator;


import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.records.MetricDocument;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MetricGeneratorService {

    public List<MetricDocument> generateMetrics(long numberOfMetrics, Instant fromDate, Instant toDate, String metricName, List values, Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        List<MetricDocument> metrics = new LinkedList<>();
        long timeBetweenMetrics = (toDate.getEpochSecond() - fromDate.getEpochSecond()) / numberOfMetrics;
        Date timeStemp = new Date();
        for (int i = 0; i < numberOfMetrics; i++) {
            MetricDocument metric;
            Date time = Date.from(fromDate.plusMillis(timeBetweenMetrics * i));
            metric = new MetricDocument(metricName, valuesMapGenerator(values), timeStemp, tags, time);
            metrics.add(metric);
        }
        return metrics;
    }

    private Map<MetricEnums.MetricValues, Number> valuesMapGenerator(List<Number> values) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        Iterator itr = MetricEnums.MetricValues.collectionOfMetricValues().iterator();
        int number = new Random().nextInt(values.size());
        for (int i = 0; i < number; i++) {
            map.put((MetricEnums.MetricValues) itr.next(), values.get(i));
        }
        return map;
    }
}
