package presidio.monitoring.datadog;

import com.timgroup.statsd.*;
import fortscale.utils.logging.Logger;
import presidio.monitoring.records.MetricDocument;

import java.util.List;
import java.util.stream.Collectors;

import static presidio.monitoring.sdk.api.services.enums.MetricEnums.MetricValues.DEFAULT_METRIC_VALUE;

public class PresidioMetricDataDogService {

    private final Logger logger = Logger.getLogger(PresidioMetricDataDogService.class);
    private List<String> metricNames;
    private String hostname;
    private int port;

    public PresidioMetricDataDogService(String hostname, int port, List<String> metricNames) {
        this.metricNames = metricNames;
        this.hostname = hostname;
        this.port = port;
    }

    public int saveCount(List<MetricDocument> metricDocument) {
        List<MetricDocument> metricDocumentFiltered = metricDocument.stream().filter(this::shouldBeSent).collect(Collectors.toList());
        metricDocumentFiltered.forEach(metric -> saveCount(metric, new String[]{}));
        return metricDocumentFiltered.size();
    }

    private void saveCount(MetricDocument metricDocument, String[] metricTags) {
        StatsDClient statsDClient = createClient(metricTags);
        statsDClient.count(metricDocument.getName(), metricDocument.getValue().get(DEFAULT_METRIC_VALUE).longValue());
        statsDClient.close();
    }

    private boolean shouldBeSent(MetricDocument metric){
        return metricNames.contains(metric.getName());
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
