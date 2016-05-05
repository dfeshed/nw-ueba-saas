package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;

import java.util.concurrent.TimeoutException;

/**
 * Created by tomerd on 31/12/2015.
 */
public class KafkaSender implements IKafkaSender{

	private static Logger logger = Logger.getLogger(KafkaSender.class);

	private int maxSize;
	private int messageCounter;
	private KafkaEventsWriter streamWriter;
	private String topic;
	private String partitionKey;
	private IKafkaSynchronizer kafkaSynchronize;

	public KafkaSender(IKafkaSynchronizer kafkaSynchronize, int maxSize, String topic, String partitionKey) {
		this.maxSize = maxSize;
		messageCounter = 0;
		this.topic = topic;
		this.partitionKey = partitionKey;
		streamWriter = new KafkaEventsWriter(topic);
		this.kafkaSynchronize = kafkaSynchronize;
	}

	public void shutDown() {
		try {
			streamWriter.close();
		} catch (Exception ex) {
			logger.error("Error while closing the kafka writer. Error {}", ex.getMessage());
		}
	}

	@Override public void callSynchronizer(long epochTime) throws TimeoutException {
		kafkaSynchronize.synchronize(epochTime);
	}

	public void send(String messageStr, long epochTime)
			throws Exception {
		logger.debug("sending message to topic {} - {} ", topic, messageStr);
		try {
			streamWriter.send(partitionKey, messageStr);
			messageCounter++;
		} catch (Exception ex) {
			logger.error("failed to send message to topic {}", topic);
			throw new Exception(ex);
		}

		if (messageCounter == maxSize) {
			if (kafkaSynchronize != null) {
				logger.info("{} messages sent, waiting for last message time {}", messageCounter, epochTime);
				callSynchronizer(epochTime);
			}
			messageCounter = 0;
		}
	}

}