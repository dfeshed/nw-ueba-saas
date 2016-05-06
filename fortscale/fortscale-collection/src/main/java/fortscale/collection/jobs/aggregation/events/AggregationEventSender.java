package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.IAggregationEventSender;
import fortscale.utils.kafka.KafkaSender;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by tomerd on 31/12/2015.
 */
public class AggregationEventSender implements IAggregationEventSender {

	private static Logger logger = Logger.getLogger(AggregationEventSender.class);

	protected static final String EPOCH_TIME_FIELD_JOB_PARAMETER = "start_time_unix";

	private static final String FTOPIC = "fortscale-aggregated-feature-f-event";
	private static final String PTOPIC = "fortscale-aggregated-feature-p-event";

	private KafkaSender fTopicKafkaSender;
	private KafkaSender pTopicKafkaSender;
	private AggregationEventSynchronizer aggrEventsSynchronizer;

	private long messagesCounter;
	private long totalMessegesCounter;
	private long fTypeMessagesCounter;
	private int  batchSize;

	public AggregationEventSender(int batchSize, String jobClassToMonitor, String jobToMonitor,
			long timeToWaitInSeconds) throws TimeoutException {

		aggrEventsSynchronizer = new AggregationEventSynchronizer(jobClassToMonitor,
				jobToMonitor, TimeUnit.SECONDS.toMillis(timeToWaitInSeconds));

		fTopicKafkaSender = new KafkaSender(null, batchSize, FTOPIC, "");
		pTopicKafkaSender = new KafkaSender(null, batchSize, PTOPIC, "");
		this.messagesCounter = 0;
		this.totalMessegesCounter = 0;
		this.fTypeMessagesCounter = 0;
		this.batchSize = batchSize;
	}

    @Override
	public void send(boolean isOfTypeF, JSONObject event) throws Exception{
		String eventValue = event.toJSONString(JSONStyle.NO_COMPRESS);
		long timestamp = event.getAsNumber(EPOCH_TIME_FIELD_JOB_PARAMETER).longValue();
		String topicToSend = FTOPIC;
		try {
			if (isOfTypeF){
				fTopicKafkaSender.send(eventValue, timestamp);
				fTypeMessagesCounter++;
			}
			else {
				topicToSend = PTOPIC;
				pTopicKafkaSender.send(eventValue, timestamp);
			}
			messagesCounter++;
			totalMessegesCounter++;

			if (messagesCounter == batchSize) {
				logger.info("{} messages sent, waiting for last message time {}", messagesCounter, timestamp);
				throttle(false);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to send message to topic {}. Error: {}", topicToSend, ex.getMessage());
			throw new Exception(ex);
		}
	}


    @Override
	public void throttle(boolean wailtForEntgityEventsToBeSavedInMongo) throws TimeoutException {
		aggrEventsSynchronizer.throttle(totalMessegesCounter, fTypeMessagesCounter);
		messagesCounter = 0;
		if(wailtForEntgityEventsToBeSavedInMongo) {
			aggrEventsSynchronizer.waitForAllEntityEventDataToBeSavedInMongo();
		}
	}


	public AggregationEventSynchronizer getAggrEventsSynchronizer() {
		return aggrEventsSynchronizer;
	}

	public void shutDown() {
		try {
			fTopicKafkaSender.shutDown();
			pTopicKafkaSender.shutDown();
		} catch (Exception ex) {
			logger.error("Error while closing the kafka writer. Error {}", ex.getMessage());
		}
	}


}