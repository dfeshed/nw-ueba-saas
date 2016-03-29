package fortscale.utils.kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleMetricsCountersReader implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(SimpleMetricsCountersReader.class);

    private static final String HEADER = "header";
    private static final String JOB_NAME = "job-name";
    private static final String METRICS_TOPIC = "metrics";
    private static final int TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024000;
    private final String headerToCheck;
    private final String jobToCheck;
    private final int waitTimeBetweenMetricsChecks;
    private final int checkRetries;
    private final String zookeeper;
    private final int port;
    private final String clientName;

    private SimpleConsumer consumer;
    private Collection<String> metricsToCapture;
    private Map<String, Object> capturedMetrics;
    private boolean continueToRun;
    private MetricsResults lastMetricsResults;

    /**
     * @param zookeeper                    zookeeper server
     * @param port                         port for the server
     * @param headerToCheck                name of header in the json message
     * @param clientName                   the name of this consumer
     * @param jobToCheck                   samza job to listen to
     * @param waitTimeBetweenMetricsChecks time in millis to wait between checks
     * @param checkRetries                 max number of retries
     */
    public SimpleMetricsCountersReader(String zookeeper, int port, String headerToCheck, String clientName, String jobToCheck,
                                       int waitTimeBetweenMetricsChecks, int checkRetries, Collection<String> metricsToCapture) {

        Assert.hasText(clientName);
        Assert.hasText(zookeeper);
        Assert.isTrue(port>0);
        Assert.hasText(headerToCheck);
        Assert.hasText(jobToCheck);
        Assert.isTrue(waitTimeBetweenMetricsChecks > 0);
        Assert.isTrue(checkRetries >0);
        Assert.notEmpty(metricsToCapture);
        metricsToCapture.forEach(Assert::hasText);

        this.zookeeper = zookeeper;
        this.port = port;
        this.clientName = clientName;
        this.headerToCheck = headerToCheck;
        this.jobToCheck = jobToCheck;
        this.waitTimeBetweenMetricsChecks = waitTimeBetweenMetricsChecks;
        this.checkRetries = checkRetries;
        this.metricsToCapture = metricsToCapture;

        capturedMetrics = new HashMap<>();
    }

    public Object getMetric(String metric) {
        return capturedMetrics.get(metric);
    }

    public void stop() {
        continueToRun = false;
    }


    public void run() {

        continueToRun = true;

        try {
            consumer = new SimpleConsumer(zookeeper, port, TIMEOUT, BUFFER_SIZE, clientName);
            long offset = 0, lastOffset = -1, currentTry = 0;

            while (continueToRun && currentTry < checkRetries) {
                lastMetricsResults = testMetrics(consumer, offset, headerToCheck, jobToCheck);
                offset = lastMetricsResults.getOffset();
                if (offset == lastOffset) {
                    try {
                        logger.info("waiting for metrics topic to refresh");
                        Thread.sleep(waitTimeBetweenMetricsChecks);
                        currentTry++;
                    } catch (InterruptedException e) {
                        logger.error("wait for metrics has been interrupted. stopping...", e);
                        return;
                    }
                } else {
                    currentTry = 0;
                }
                lastOffset = offset;
            }
        } finally {
            if(consumer != null) {
                consumer.close();
            }
        }
        logger.info("Stopped reading metrics after {} retries", checkRetries);
        consumer.close();
    }


    private MetricsResults testMetrics(SimpleConsumer consumer, long offset, String headerToCheck, String jobToCheck) {

        int partition = 0;

        FetchRequest fetchRequest = new FetchRequestBuilder()
                .clientId("clientId")
                .addFetch(METRICS_TOPIC, partition, offset, BUFFER_SIZE)
                .build();
        FetchResponse messages = consumer.fetch(fetchRequest);
        if (messages.hasError()) {
            String errorMsg = String.format("failed to read from metrics topic - %s", messages.errorCode(METRICS_TOPIC,
                    partition));
            logger.error(errorMsg);
            return new MetricsResults(false, offset,errorMsg);
        }
        long nextOffset = offset;
        for (MessageAndOffset msg : messages.messageSet(METRICS_TOPIC, partition)){
            nextOffset = msg.nextOffset();
            String message = convertPayloadToString(msg);
            JSONObject metrics = getMetrics(message, jobToCheck, headerToCheck);
            if (checkMetrics(metrics)) {
                return new MetricsResults(true, nextOffset, null);
            }
        }

        return new MetricsResults(false, nextOffset,null);
    }

    private boolean checkMetrics(JSONObject metrics) {
        if (metrics == null) {
            return false;
        }

        metricsToCapture.stream().filter(metrics::has)
                .forEach(metricToCapture -> capturedMetrics.put(
                        metricToCapture, metrics.get(metricToCapture)));

        return capturedMetrics.size() == metricsToCapture.size();
    }

    /**
     * @param rawMsg a raw Kafka message
     * @return a String representation of the given raw Kafka message
     */
    private static String convertPayloadToString(MessageAndOffset rawMsg) {
        ByteBuffer buf = rawMsg.message().payload();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        return new String(dst);
    }

    /**
     * @param message       a message from the metrics stream
     * @param jobName       requested job name
     * @param metricsHeader requested header where the wanted metrics are
     * @return the wanted metrics if the header exists and the message is from the requested job, null otherwise
     */
    private static JSONObject getMetrics(String message, String jobName, String metricsHeader) {
        JSONObject metrics = null;
        try {
            JSONObject messageJson = new JSONObject(message);
            if (messageJson.getJSONObject(HEADER).getString(JOB_NAME).equals(jobName)) {
                metrics = messageJson.getJSONObject(METRICS_TOPIC).getJSONObject(metricsHeader);
            }
        } catch (Exception e) {
            metrics = null;
        }
        return metrics;
    }

    public static class MetricsResults{
        boolean found;
        long offset;
        String error = null;

        public MetricsResults(boolean found, long offset, String error) {
            this.found = found;
            this.offset = offset;
            this.error = error;
        }

        public boolean isFound() {
            return found;
        }

        public long getOffset() {
            return offset;
        }

        public String getError() {
            return error;
        }
    }
}
