package fortscale.utils.kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MetricsReader {

    private static Logger logger = LoggerFactory.getLogger(MetricsReader.class);

    private static final String HEADER = "header";
    private static final String JOB_NAME = "job-name";
    private static final String METRICS_TOPIC = "metrics";
    private static final int TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024000;

    /**
     * @param zookeeper                    zookeeper server
     * @param port                         port for the server
     * @param headerToCheck                name of header in the json message
     * @param jobToCheck                   samza job to listen to
     * @param decider                      decides whether the wanted metrics have arrived
     * @param waitTimeBetweenMetricsChecks time in millis to wait between checks
     * @param checkRetries                 max number of retries
     * @return true iff the wanted metrics have arrived
     */
    public static boolean waitForMetrics(
            String zookeeper, int port, String headerToCheck, String jobToCheck,
            IMetricsDecider decider, int waitTimeBetweenMetricsChecks, int checkRetries) {

        SimpleConsumer consumer = new SimpleConsumer(zookeeper, port, TIMEOUT, BUFFER_SIZE, "clientName");
        long offset = 0, lastOffset = -1, currentTry = 0;
        int partition = 0;

        while (currentTry < checkRetries) {
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId("clientId")
                    .addFetch(METRICS_TOPIC, partition, offset, BUFFER_SIZE)
                    .build();
            FetchResponse messages = consumer.fetch(fetchRequest);
            if (messages.hasError()) {
                logger.error("failed to read from metrics topic - {}", messages.errorCode(METRICS_TOPIC, partition));
                consumer.close();
                return false;
            }
            for (MessageAndOffset msg : messages.messageSet(METRICS_TOPIC, partition)) {
                long currentOffset = msg.offset();
                if (currentOffset < offset) {
                    logger.warn("found an old offset: " + currentOffset + " expecting: " + offset);
                    break;
                }
                String message = convertPayloadToString(msg);
                JSONObject metrics = getMetrics(message, jobToCheck, headerToCheck);
                if (decider.decide(metrics)) {
                    consumer.close();
                    return true;
                }
                offset = msg.nextOffset();
            }
            if (offset == lastOffset) {
                try {
                    logger.info("waiting for metrics topic to refresh");
                    Thread.sleep(waitTimeBetweenMetricsChecks);
                    currentTry++;
                } catch (InterruptedException e) {
                    logger.error("wait for metrics has been interrupted. stopping...", e);
                    consumer.close();
                    return false;
                }
            } else {
                currentTry = 0;
            }
            lastOffset = offset;
        }
        logger.error("failed to get metrics data in {} retries", checkRetries);
        consumer.close();
        return false;
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
}