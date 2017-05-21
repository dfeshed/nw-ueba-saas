package fortscale.utils.kafka;

import fortscale.utils.ConversionUtils;
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
import java.util.List;

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

        SimpleConsumer consumer = null;

        try {
            consumer = new SimpleConsumer(zookeeper, port, TIMEOUT, BUFFER_SIZE, "clientName");
            long offset = 0, lastOffset = -1, currentTry = 0;

            while (currentTry < checkRetries) {
                MetricsResults metricsResults = testMetrics(consumer, offset, headerToCheck, jobToCheck, decider);
                if(metricsResults.isFound()){
                    return true;
                } else if(metricsResults.getError() != null){
                    return false;
                }

                offset = metricsResults.getOffset();
                if (offset == lastOffset) {
                    try {
                        logger.info("waiting for metrics topic to refresh");
                        Thread.sleep(waitTimeBetweenMetricsChecks);
                        currentTry++;
                    } catch (InterruptedException e) {
                        logger.error("wait for metrics has been interrupted. stopping...", e);
                        return false;
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
        logger.error("failed to get metrics data in {} retries", checkRetries);
        consumer.close();
        return false;
    }

    public static MetricsResults fetchMetric(long offset,
                                      String zookeeper, int port, String headerToCheck, String jobToCheck,
                                      IMetricsDecider decider) {
        SimpleConsumer consumer = null;

        try {
            consumer = new SimpleConsumer(zookeeper, port, TIMEOUT, BUFFER_SIZE, "clientName");
            return testMetrics(consumer, offset, headerToCheck, jobToCheck, decider);
        } finally {
            if(consumer != null) {
                consumer.close();
            }
        }

    }

    public static long getCounterMetricsSum(List<String> metrics, String zookeeper, int port,  String headerToCheck,
            String jobToCheck) {
        long counterMetricsSum = 0;

        CaptorMetricsDecider captor = new CaptorMetricsDecider(metrics);
        long offset;

        MetricsReader.MetricsResults metricsResults = new MetricsReader.MetricsResults(false,0,null);
        do {
            offset = metricsResults.getOffset();
            metricsResults = MetricsReader.fetchMetric(offset, zookeeper, port, headerToCheck, jobToCheck, captor);

            if(metricsResults.isFound()) {
		counterMetricsSum = 0;
                for (Object capturedMetric : captor.getCapturedMetricsMap().values()) {
                    Long counter = ConversionUtils.convertToLong(capturedMetric);
                    if (counter != null) {
                        counterMetricsSum += counter;
                    }
                }
            }
        }while(metricsResults.getOffset() > offset);// Looking for the last metric message.

        return counterMetricsSum;
    }

    private static MetricsResults testMetrics(SimpleConsumer consumer, long offset,
            String headerToCheck, String jobToCheck,
            IMetricsDecider decider) {


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
            if (decider.decide(metrics)) {
                return new MetricsResults(true, nextOffset, null);
            }
        }

        return new MetricsResults(false, nextOffset,null);
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
        } catch (JSONException e) {
            metrics = null;
            logger.warn("could not find json object {}", e);
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
