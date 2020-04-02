package presidio.monitoring.datadog;

import com.timgroup.statsd.*;
import fortscale.utils.logging.Logger;
import presidio.monitoring.records.MetricDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PresidioMetricDataDogService {

    private final Logger logger = Logger.getLogger(PresidioMetricDataDogService.class);
    private List<String> metricsShouldBeSent;
    private String hostname;
    private int port;

    public PresidioMetricDataDogService(String hostname, int port, List<String> metricsShouldBeSent) {
        this.metricsShouldBeSent = metricsShouldBeSent;
        this.hostname = hostname;
        this.port = port;
    }

    public int saveCount(List<MetricDocument> metricDocument) {
        Map<String, Long> metrics = new HashMap<>();
        metricDocument.forEach(metric -> metrics.putAll(metricDocumentToMetricsMap(metric)));

        Map<String, Long> metricsFiltered = metrics.entrySet().stream()
                .filter(entry -> shouldBeSent(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        saveCount(metricsFiltered, new String[]{});
        return metricsFiltered.size();
    }

    private void saveCount(Map<String, Long> metricsToSave, String[] metricTags){
        StatsDClient statsDClient = createClient(metricTags);
        metricsToSave.forEach((metricName, metricValue) -> statsDClient.count(metricName, metricValue));
        statsDClient.close();
    }

    private boolean shouldBeSent(String metricName){
        return metricsShouldBeSent.contains(metricName);
    }

    private Map<String, Long> metricDocumentToMetricsMap(MetricDocument metricDocument){
        Map<String, Long> metricsMap = new HashMap<>();
        metricDocument.getValue().forEach((metricKey, metricValue) ->
                metricsMap.put(String.format("%s.%s", metricDocument.getName(), metricKey), metricValue.longValue()));
        return metricsMap;
    }

    /**
     * Returns an initialized instance of the StatsDClient. If StatsDClient initialized succeed
     * a NonBlockingStatsDClient is created. If failed it creates a NoOpStatsDClient which
     * contains all empty methods
     *
     * @return initialized StatsDClient
     */
    private StatsDClient createClient(String[] metricTags) {
        StatsDClientErrorHandler errorHandler = new StatsDClientErrorHandler() {
            @Override
            public void handle(Exception exception) {
                logger.error("Error sending to statsd: " + exception);
            }
        };
        try {
            String prefix = "presidio";
            return new NonBlockingStatsDClient(prefix, hostname, port, metricTags, errorHandler);
        } catch (Exception ex) {
            logger.error(String.format("Unable to open connection to statsd at [" + hostname + ":" + port + "]; using NoOp client.", ex));
            return new NoOpStatsDClient();
        }
    }
}
