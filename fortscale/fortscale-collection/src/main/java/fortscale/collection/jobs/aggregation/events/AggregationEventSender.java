package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.IAggregationEventSender;
import fortscale.utils.kafka.MultiTopicsKafkaSender;
import fortscale.utils.kafka.NumberOfMessagesSynchronizer;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tomerd on 31/12/2015.
 */
public class AggregationEventSender implements IAggregationEventSender {

	private static Logger logger = Logger.getLogger(AggregationEventSender.class);

	protected static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "start_time_unix";

	private static final String FTOPIC = "fortscale-aggregated-feature-f-event";
	private MultiTopicsKafkaSender multiTopicsKafkaSender;
	private static final String PTOPIC = "fortscale-aggregated-feature-p-event";

	public AggregationEventSender(int batchSize, String jobClassToMonitor, String jobToMonitor,
			long timeToWaitInSeconds) throws TimeoutException {

		AggregationEventSynchronizer aggrEventsSynchronizer = new AggregationEventSynchronizer(jobClassToMonitor,
				jobToMonitor, TimeUnit.SECONDS.toMillis(timeToWaitInSeconds));

		// Create multi-topics kakfa sender.
		// As our application acts as single node, there's no need in the partition key
		multiTopicsKafkaSender = new MultiTopicsKafkaSender(aggrEventsSynchronizer, batchSize,
				Arrays.asList(FTOPIC, PTOPIC), "");
	}

	@Override public void send(boolean isOfTypeF, JSONObject event){
		String eventValue = event.toJSONString(JSONStyle.NO_COMPRESS);
		long timestamp = event.getAsNumber(EPOCH_TIME_FIELD_JOB_PARAMETER).longValue();
		String topicToSend;
		if (isOfTypeF){
			topicToSend = FTOPIC;
		}
		else {
			topicToSend = PTOPIC;
		}

		try {
			multiTopicsKafkaSender.send(topicToSend, eventValue, timestamp);
		}
		catch (Exception ex) {
			logger.error("Failed to send message to topic {}. Error: {}", topicToSend, ex.getMessage());
		}
	}

	@Override public void callSynchronizer(long epochTime) throws TimeoutException {
		multiTopicsKafkaSender.callSynchronizer(epochTime);
	}

}