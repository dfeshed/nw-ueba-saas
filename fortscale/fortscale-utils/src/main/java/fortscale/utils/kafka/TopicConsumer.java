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

	public void shutdown() {
		if (consumer != null) consumer.shutdown();
	}

	/**
     *
	 * Reads messages from metrics topic, filter them, and wait until the relevant ones stabilize on a fixed value
	 * in a given timeout. if all of that successes, return true
	 * @param jobToCheck
	 * @param headerToCheck
	 * @param metricsToExtract
	 * @param timeoutInSeconds
	 * @return true iff all successes, false otherwise.
	 * @throws Exception
     *
	 */
	/*public boolean run(String jobToCheck, String headerToCheck, String metricsToExtract,int timeoutInSeconds,
					   int successIterationsUntilStabilize)throws Exception {
        boolean ok = false;
		ExecutorService exService = Executors.newSingleThreadExecutor();
		Future<Boolean> futureOk = exService.submit(new SamzaMetricsReader(jobToCheck, headerToCheck, metricsToExtract,
                successIterationsUntilStabilize));
		try {
			if (timeoutInSeconds > 0) {
				ok = futureOk.get(timeoutInSeconds, TimeUnit.SECONDS);
			} else {
				ok = futureOk.get();
			}
		} catch (TimeoutException e) {
			// in the case of a timeout
			logger.error("Samza metrics reader reached a timeout of {} seconds. Canceling ...", timeoutInSeconds);
			throw new Exception("Samza's metrics " + metricsToExtract + " hasn't stabilized in the given time: " +
                    timeoutInSeconds + " seconds. aborting.");
		} finally {
			exService.shutdownNow();
		}
		return ok;
	}*/

	/**
     *
	 * Inner class uses for running readSamzaMetrics in a different thread.
	 * this is because if there is an error on the topic, the consumer will wait endlessly.
     *
	 */
	/*private class SamzaMetricsReader implements Callable {

		String jobToCheck;
		String headerToCheck;
		String metricsToExtract;
		int successIterationsUntilStabilize;

		SamzaMetricsReader(String jobToCheck, String headerToCheck, String metricsToExtract,
                           int successIterationsUntilStabilize) {
			this.jobToCheck = jobToCheck;
			this.headerToCheck = headerToCheck;
			this.metricsToExtract = metricsToExtract;
			this.successIterationsUntilStabilize = successIterationsUntilStabilize;
		}

		public Boolean call()throws Exception {
			return readSamzaMetrics(jobToCheck, headerToCheck, metricsToExtract, successIterationsUntilStabilize);
		}
	}*/

    /**
     * Consume messages from samza 'metrics' topic. get the relevant metrics, and wait for them to stabilize.
     * wait to stabilize - when we get two consecutive identical metricsToExtract, means we are done -
     * all messages have been read. this is a bit naive approach, but satisfy.
     * @param jobToCheck
     * @param headerToCheck
     * @param metricsToExtract
     * @return true if the metrics have stabilized.
     */
    public String readSamzaMetric(String jobToCheck, String headerToCheck, String metricsToExtract) {
        int topicCount = 1;
        Map<String, Integer> topicCountMap = new HashMap();
        topicCountMap.put(topic, new Integer(topicCount));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        Map <String, String> metricData;
        for (final KafkaStream stream : streams) {
            String currMetric;
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                currMetric = new String(it.next().message());
                metricData = getMetricData(currMetric, headerToCheck, metricsToExtract);
                if (metricData.containsKey("job-name") && metricData.get("job-name").equals(jobToCheck)) {
                    return metricData.get(metricsToExtract);
                }
            }
        }
        return "";
    }

	/*private boolean readSamzaMetrics(String jobToCheck, String headerToCheck, String metricsToExtract,
                                     int successIterationsUntilStabilize) {

		int topicCount = 1;
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(topicCount));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		Map <String, String> metricData;
		Map <String, String> prevMetricData = new HashMap<String, String>();
		for (final KafkaStream stream : streams) {
			String currMetric;
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			int counter = 0;
			while (it.hasNext()) {
				currMetric = new String(it.next().message());
				metricData = getMetricData(currMetric, headerToCheck, metricsToExtract);
				//check if the metrics stabilize. if stable - log the data and return true.
				// else - it should keep running and eventually will be terminated from outside (due to timeout).
				if (metricData.containsKey("job-name") && metricData.get("job-name").equals(jobToCheck)) {
					if (metricData.get(metricsToExtract).equals(prevMetricData.get(metricsToExtract))) {
						logger.info(metricsToExtract + ":" + metricData.get(metricsToExtract));
						counter ++;
					} else {
						counter = 0;
					}
					if (counter == successIterationsUntilStabilize){
						logger.info(metricsToExtract + ":" + metricData.get(metricsToExtract) + " has stabilized. ");
						return true;
					}
					prevMetricData = new HashMap(metricData);
				}
			}
		}
		return false;
	}*/

	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "10000");
		props.put("zookeeper.sync.time.ms", "2000");
		props.put("auto.commit.interval.ms", "10000");
		return new ConsumerConfig(props);
	}

	/**
	 * Reads a metric json and extract from it the data we want (e.g. number of process calls)
	 * @param metric a message from metrics stream
	 * @param header the header within the metrics to extract are.
	 * @param metricsToExtract requested data
	 * @return data
	 */
	public static Map <String, String> getMetricData(String metric, String header, String metricsToExtract) {
		Map <String, String> metricaData = new HashMap();
        if (metric.contains(header)) { // otherwise, irrelevant metric
            String currValue;
            try {
                JSONObject bigJSON = new JSONObject(metric);
                JSONObject innerJSON =  bigJSON.getJSONObject("metrics");
                //mark which job's metrics we've just read (it's in the header of each metric)
                metricaData.put("job-name", bigJSON.getJSONObject(headerString).getString("job-name"));
                //extract the relevant data from the metrics json
                currValue = innerJSON.getJSONObject(header).getString(metricsToExtract);
                metricaData.put(metricsToExtract, currValue);
            } catch(JSONException je) {
                logger.error(je.getMessage());
            }
        }
		return metricaData;
	}

}