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

    private static final int waitTimeBetweenMetricsChecks = 60;

    public boolean listenToMetricsTopic(String headerToCheck, String jobToCheck, String metricsToExtract,
                                        String lastMessageTime) {
        SimpleConsumer consumer = new SimpleConsumer("localhost", 9092, 10000, 1024000, "clientId");
        long offset = 0, lastoffset;
        while (true) {
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId("clientName")
                    .addFetch("metrics", 0, offset, 100000)
                    .build();
            FetchResponse messages = consumer.fetch(fetchRequest);
            for (MessageAndOffset msg : messages.messageSet("metrics", 0)) {
                String message = new String(msg.message().payload().array(), Charset.forName("UTF-8"));
                Map<String, String> metricData = getMetricData(message, headerToCheck, metricsToExtract);
                if (metricData.containsKey("job-name") && metricData.get("job-name").equals(jobToCheck)) {
                    if (metricData.get(metricsToExtract).equals(lastMessageTime)) {
                        logger.info(metricsToExtract + ":" + metricData.get(metricsToExtract) + " reached ");
                        return true;
                    }
                }
                offset = msg.nextOffset();
            }
            lastoffset = offset;
            if (offset == lastoffset) {
                try {
                    Thread.sleep(waitTimeBetweenMetricsChecks * 1000L);
                } catch (InterruptedException e) {
                    logger.info("metrics counting of {} has been interrupted. Stopping...", metricsToExtract);
                    return false;
                }
            }
        }
    }

    /**
     * reads a metric json and extract from it the data we want (e.g. number of process calls)
     * @param metric a message from metrics stream
     * @param header the header within the metrics to extract are.
     * @param metricsToExtract requested data
     * @return data
     */
    public static Map <String, String> getMetricData(String metric, String header, String metricsToExtract) {
        Map <String, String> metricaData = new HashMap();
        if (metric.contains(header)){ // otherwise, irrelevant metric
            String currValue;
            try{
                JSONObject bigJSON = new JSONObject(metric);
                JSONObject innerJSON =  bigJSON.getJSONObject("metrics");
                //mark which job's metrics we've just read (it's in the header of each metric)
                metricaData.put("job-name", bigJSON.getJSONObject("header").getString("job-name"));
                //extract the relevant data from the metrics json
                //find the relevant container
                currValue = innerJSON.getJSONObject(header).getString(metricsToExtract);
                metricaData.put(metricsToExtract, currValue);
            }
            catch(JSONException je) {
                logger.error(je.getMessage());
            }
        }
        return metricaData;
    }

}