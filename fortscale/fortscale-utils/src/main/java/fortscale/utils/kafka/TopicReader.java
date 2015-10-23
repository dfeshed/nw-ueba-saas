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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class TopicReader {

    private static Logger logger = LoggerFactory.getLogger(TopicReader.class);

    private static final String HEADER = "header";
    private static final String JOB_NAME = "job-name";
    private static final String TOPIC = "metrics";

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
    public boolean listenToMetricsTopic(String zookeeper, int port, String headerToCheck, String jobToCheck,
                                        String metricsToExtract, String lastMessageTime,
                                        int waitTimeBetweenMetricsChecks) {
        SimpleConsumer consumer = new SimpleConsumer(zookeeper, port, 10000, 1024000, "clientId");
        int partition = 0;
        long offset = 0, lastoffset;
        while (true) {
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId("clientName")
                    .addFetch(TOPIC, partition, offset, 100000)
                    .build();
            FetchResponse messages = consumer.fetch(fetchRequest);
            if (messages.hasError()) {
                logger.error("failed to read from metrics topic - {}", messages.errorCode(TOPIC, partition));
                return false;
            }
            for (MessageAndOffset msg : messages.messageSet(TOPIC, partition)) {
                String message = new String(msg.message().payload().array(), Charset.forName("UTF-8"));
                Map<String, String> metricData = getMetricData(TOPIC, message, headerToCheck, metricsToExtract);
                if (metricData.containsKey(JOB_NAME) && metricData.get(JOB_NAME).equals(jobToCheck)) {
                    if (metricData.get(metricsToExtract).equals(lastMessageTime)) {
                        logger.info(metricsToExtract + ":" + metricData.get(metricsToExtract) + " reached");
                        return true;
                    }
                }
                offset = msg.nextOffset();
            }
            lastoffset = offset;
            if (offset == lastoffset) {
                try {
                    logger.info("waiting for metrics topic to refresh");
                    Thread.sleep(waitTimeBetweenMetricsChecks);
                } catch (InterruptedException e) {
                    logger.info("metrics counting of {} has been interrupted. Stopping...", metricsToExtract);
                    return false;
                }
            }
        }
    }

    /**
     * reads a metric json and extract from it the data we want (e.g. number of process calls)
     * @param metric            a message from metrics stream
     * @param header            the header within the metrics to extract are.
     * @param metricsToExtract  requested data
     * @return data
     */
    private Map <String, String> getMetricData(String topic, String metric, String header, String metricsToExtract) {
        Map <String, String> metricaData = new HashMap();
        if (metric.contains(header)) {
            String currValue;
            try {
                JSONObject bigJSON = new JSONObject(metric);
                JSONObject innerJSON = bigJSON.getJSONObject(topic);
                metricaData.put(JOB_NAME, bigJSON.getJSONObject(HEADER).getString(JOB_NAME));
                currValue = innerJSON.getJSONObject(header).getString(metricsToExtract);
                metricaData.put(metricsToExtract, currValue);
            } catch (JSONException ex) {
                logger.error(ex.getMessage());
            }
        }
        return metricaData;
    }

}