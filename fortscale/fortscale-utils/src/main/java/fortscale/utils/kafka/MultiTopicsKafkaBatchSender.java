package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by tomerd on 31/12/2015.
 */
public class MultiTopicsKafkaBatchSender implements IKafkaSender{

	private static Logger logger = Logger.getLogger(MultiTopicsKafkaBatchSender.class);

	private int messagesCounter;
	private int maxSize;
	private Map<String, KafkaBatchSender> kafkaWriters;
	private String partitionKey;
	private IKafkaSynchronizer kafkaSynchronize;

	public MultiTopicsKafkaBatchSender(IKafkaSynchronizer kafkaSynchronize, int maxSize, List<String> topics, String partitionKey) {
		this.messagesCounter = 0;
		this.maxSize = maxSize;
		this.partitionKey = partitionKey;
		kafkaSynchronize = kafkaSynchronize;
		kafkaWriters = new HashMap<String, KafkaBatchSender>();
		for (String topic : topics) {
			kafkaWriters.put(topic, new KafkaBatchSender(kafkaSynchronize, maxSize, topic, partitionKey));
		}
	}

	public void shutDown() {
		try {
			for (KafkaBatchSender kafkaBatchSender : kafkaWriters.values()){
				kafkaBatchSender.shutDown();
			}
		} catch (Exception ex) {}
	}

	public void flushMessages() throws Exception {
		logger.info("flushing {} messages", messagesCounter);
		long latestEpochTimeSent = 0;
		int messagesSent = 0;

		for (KafkaBatchSender writer : kafkaWriters.values()) {
			writer.flushMessages();
		}

		if (latestEpochTimeSent > 0) {
			logger.info("{} messages sent, waiting for last message time {}", messagesSent, latestEpochTimeSent);
			callSynchronize(kafkaSynchronize, latestEpochTimeSent);
		}
		logger.info("finished flushing, clearing queue");
		messagesCounter = 0;
	}

	public void send(String topic, String messageStr, long epochTime) throws Exception{
		kafkaWriters.get(topic).send(messageStr, epochTime);
		messagesCounter++;
		if (messagesCounter < maxSize) {
			return;
		}
		flushMessages();
	}

	private void callSynchronize(IKafkaSynchronizer kafkaSynchronize, long latestEpochTimeSent) {
		kafkaSynchronize.synchronize(latestEpochTimeSent);
	}
}
