package fortscale.utils.kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToLong;

public class MetricsReader {

    private static Logger logger = LoggerFactory.getLogger(MetricsReader.class);

    private static final String HEADER = "header";
    private static final String JOB_NAME = "job-name";
    private static final String METRICS_TOPIC = "metrics";

    /***
     *
     * This method listens to the metrics topic for the last message time to arrive
     *
     * @param zookeeper         zookeeper server
     * @param port              port for the server
     * @param headerToCheck     name of header in the json message
     * @param jobToCheck        samza job to listen to
     * @param metricsToExtract  name of metrics to listen to
     * @param lastMessageTime   message time to compare
     * @return
     */
    public static boolean waitForMetrics(String zookeeper, int port, String headerToCheck, String jobToCheck,
                                  String metricsToExtract, long lastMessageTime,
                                  int waitTimeBetweenMetricsChecks, int checkRetries) {
        SimpleConsumer consumer = new SimpleConsumer(zookeeper, port, 10000, 1024000, "clientName");
        long offset = 0, lastoffset = -1, currentTry = 0;
        int partition = 0;
        Long time = null;
        while (currentTry < checkRetries) {
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId("clientId")
                    .addFetch(METRICS_TOPIC, partition, offset, 1000000)
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
                    logger.debug("found an old offset: " + currentOffset + " expecting: " + offset);
                    consumer.close();
                    continue;
                }
                String message = convertPayloadToString(msg);
                Map<String, Object> metricData = getMetricData(message, headerToCheck, metricsToExtract);
                if (metricData.containsKey(JOB_NAME) && metricData.get(JOB_NAME).equals(jobToCheck)) {
                    time = convertToLong(metricData.get(metricsToExtract));
                    if (time != null && time == lastMessageTime) {
                        logger.info(metricsToExtract + ":" + time + " reached");
                        consumer.close();
                        return true;
                    }
                }
                offset = msg.nextOffset();
            }
            if (offset == lastoffset) {
                try {
                    logger.info("waiting for metrics topic to refresh");
                    if (time != null) {
                        logger.info("last message time is {}", time);
                    }
                    Thread.sleep(waitTimeBetweenMetricsChecks);
                    currentTry++;
                } catch (InterruptedException e) {
                    logger.error("metrics counting of {} has been interrupted. Stopping...", metricsToExtract);
                    consumer.close();
                    return false;
                }
            }
            lastoffset = offset;
        }
        logger.error("failed to get metrics data in {} retries", checkRetries);
        consumer.close();
        return false;
    }

    /***
     *
     * This method converts the kafka message to string
     *
     * @param rawMsg  raw kafka message
     * @return
     */
    private static String convertPayloadToString(MessageAndOffset rawMsg) {
        ByteBuffer buf = rawMsg.message().payload();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        String str = new String(dst);
        return str;
    }

    /**
     * reads a metric json and extract from it the data we want (e.g. number of process calls)
     * @param metric            a message from metrics stream
     * @param header            the header within the metrics to extract are.
     * @param metricsToExtract  requested data
     * @return data
     */
    private static Map <String, Object> getMetricData(String metric, String header, String metricsToExtract) {
        Map <String, Object> metricaData = new HashMap();
        if (metric.contains(header)) {
            Object currValue;
            try {
                JSONObject bigJSON = new JSONObject(metric);
                JSONObject innerJSON = bigJSON.getJSONObject(METRICS_TOPIC);
                metricaData.put(JOB_NAME, bigJSON.getJSONObject(HEADER).getString(JOB_NAME));
                currValue = innerJSON.getJSONObject(header).get(metricsToExtract);
                metricaData.put(metricsToExtract, currValue);
            } catch (JSONException ex) {
                logger.error(ex.getMessage());
            }
        }
        return metricaData;
    }

}