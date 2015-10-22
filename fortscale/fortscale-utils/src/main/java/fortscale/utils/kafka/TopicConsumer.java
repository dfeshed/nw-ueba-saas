package fortscale.utils.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * this class handles all the topic consumptions from kafka - e.g. read metrics topic.
 *
 */
public class TopicConsumer {

	private static Logger logger = LoggerFactory.getLogger(TopicConsumer.class);

	private final ConsumerConnector consumer;
	private final String topic;

	private static String headerString = "header";

	@Autowired
	public TopicConsumer(String zookeeper, String groupId, String topicName) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		this.topic = topicName;
	}

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "10000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.interval.ms", "10000");
        return new ConsumerConfig(props);
    }

	private void shutdown() {
		if (consumer != null) consumer.shutdown();
        logger.info("topic consumer shut down");
	}

    /**
     * Consume messages from samza 'metrics' topic. get the relevant metrics, and wait for them to stabilize.
     * wait to stabilize - when we get two consecutive identical metricsToExtract, means we are done -
     * all messages have been read. this is a bit naive approach, but satisfy.
     * @param jobToCheck
     * @param headerToCheck
     * @param metricsToExtract
     * @return true if the metrics have stabilized.
     */
    public Object readSamzaMetric(String jobToCheck, String headerToCheck, String metricsToExtract) {
        int topicCount = 1;
        Object result = null;
        Map<String, Integer> topicCountMap = new HashMap();
        topicCountMap.put(topic, new Integer(topicCount));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        Map <String, Object> metricData;
        for (final KafkaStream stream : streams) {
            String currMetric;
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                currMetric = new String(it.next().message());
                metricData = getMetricData(currMetric, headerToCheck, metricsToExtract);
                if (metricData.containsKey("job-name") && metricData.get("job-name").equals(jobToCheck)) {
                    result = metricData.get(metricsToExtract);
                    break;
                }
            }
        }
        shutdown();
        return result;
    }

	/**
	 * Reads a metric json and extract from it the data we want (e.g. number of process calls)
	 * @param metric a message from metrics stream
	 * @param header the header within the metrics to extract are.
	 * @param metricsToExtract requested data
	 * @return data
	 */
	public static Map <String, Object> getMetricData(String metric, String header, String metricsToExtract) {
		Map <String, Object> metricaData = new HashMap();
        if (metric.contains(header)) { // otherwise, irrelevant metric
            Object currValue;
            try {
                JSONObject bigJSON = new JSONObject(metric);
                JSONObject innerJSON =  bigJSON.getJSONObject("metrics");
                //mark which job's metrics we've just read (it's in the header of each metric)
                metricaData.put("job-name", bigJSON.getJSONObject(headerString).getString("job-name"));
                //extract the relevant data from the metrics json
                currValue = innerJSON.getJSONObject(header).get(metricsToExtract);
                metricaData.put(metricsToExtract, currValue);
            } catch(JSONException ex) {
                logger.error(ex.getMessage());
            }
        }
		return metricaData;
	}

}