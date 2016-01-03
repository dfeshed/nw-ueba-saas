package fortscale.aggregation.feature.event;

import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.MetricsKafkaSynchronizer;
import fortscale.utils.kafka.MultiTopicsKafkaBatchSender;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import java.util.Arrays;

/**
 * Created by tomerd on 31/12/2015.
 */
public class AggregationEventSender implements IAggregationEventSender {

	private static Logger logger = Logger.getLogger(AggregationEventSender.class);

	protected static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "epochtimeField";

	private KafkaEventsWriter eventsWriter;

	private String fTopic;
	private MultiTopicsKafkaBatchSender multiTopicsKafkaBatchSender;
	private String pTopic;

	private int batchSize;

	public AggregationEventSender(int batchSize, String jobClassToMonitor, String jobToMonitor, int timeToWaitInMilliseconds,
			int retries){
		MetricsKafkaSynchronizer metricsKafkaSynchronizer = new MetricsKafkaSynchronizer(jobClassToMonitor,
				jobToMonitor, timeToWaitInMilliseconds, retries);
		// Create multi-topics kakfa sender.
		// As our application acts as single node, there's no need in the partition key
		multiTopicsKafkaBatchSender = new MultiTopicsKafkaBatchSender(metricsKafkaSynchronizer, batchSize,
				Arrays.asList(fTopic, pTopic), "");
	}

	@Override public void send(boolean isOfTypeF, JSONObject event){
		String eventValue = event.toJSONString(JSONStyle.NO_COMPRESS);
		long timestamp = event.getAsNumber(EPOCH_TIME_FIELD_JOB_PARAMETER).longValue();
		String topicToSend;
		if (isOfTypeF){
			topicToSend = fTopic;
		}
		else {
			topicToSend = pTopic;
		}

		try {
			multiTopicsKafkaBatchSender.send(topicToSend, eventValue, timestamp);
		}
		catch (Exception ex) {
			logger.error("Failed to send message to topic {}", topicToSend);
		}
	}
}
