package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by tomerd on 31/12/2015.
 */
public class KafkaBatchSender implements IKafkaSender{

	private static Logger logger = Logger.getLogger(KafkaBatchSender.class);

	private Queue<KafkaMessage> messages;
	private int maxSize;
	private KafkaEventsWriter streamWriter;
	private String topic;
	private String partitionKey;
	private IKafkaSynchronizer kafkaSynchronize;

	public KafkaBatchSender(IKafkaSynchronizer kafkaSynchronize, int maxSize, String topic, String partitionKey) {
		this.maxSize = maxSize;
		this.topic = topic;
		this.partitionKey = partitionKey;
		messages = new LinkedList();
		streamWriter = new KafkaEventsWriter(topic);
		this.kafkaSynchronize = kafkaSynchronize;
	}

	public void shutDown() {
		try {
			streamWriter.close();
		} catch (Exception ex) {}
	}

	public void flushMessages() throws Exception {
		logger.info("flushing {} messages", messages.size());
		long latestEpochTimeSent = 0;
		int messagesSent = 0;
		for (KafkaMessage message: messages) {
			logger.debug("sending message {} to topic {} - {} ", messagesSent, topic, message.getMessageString());
			try {
				streamWriter.send(partitionKey, message.getMessageString());
			} catch (Exception ex) {
				logger.error("failed to send message to topic {}", topic);
				throw new Exception(ex);
			}
			logger.debug("{} messages sent", messagesSent++);
			latestEpochTimeSent = message.getEpochTime();
		}
		if (latestEpochTimeSent > 0) {
			logger.info("{} messages sent, waiting for last message time {}", messagesSent, latestEpochTimeSent);
			callSynchronize(kafkaSynchronize, latestEpochTimeSent);
		}
		logger.info("finished flushing, clearing queue");
		messages.clear();
	}

	public void send(String messageStr, long epochTime)
			throws Exception {
		messages.add(new KafkaMessage(messageStr, epochTime));
		if (messages.size() < maxSize) {
			return;
		}
		flushMessages();
	}

	private void callSynchronize(IKafkaSynchronizer kafkaSynchronize, long latestEpochTimeSent) {
		kafkaSynchronize.synchronize(latestEpochTimeSent);
	}
}
