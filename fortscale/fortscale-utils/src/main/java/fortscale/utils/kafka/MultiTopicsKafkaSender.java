package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;

import java.util.*;

/**
 * Created by tomerd on 31/12/2015.
 */
public class MultiTopicsKafkaSender implements IKafkaSender{

	private static Logger logger = Logger.getLogger(MultiTopicsKafkaSender.class);

	private int messagesCounter;
	private int maxSize;
	private Map<String, KafkaSender> kafkaWriters;
	private String partitionKey;
	private IKafkaSynchronizer kafkaSynchronize;

	public MultiTopicsKafkaSender(IKafkaSynchronizer kafkaSynchronize, int maxSize, List<String> topics, String partitionKey) {
		this.messagesCounter = 0;
		this.maxSize = maxSize;
		this.partitionKey = partitionKey;
		this.kafkaSynchronize = kafkaSynchronize;
		this.kafkaWriters = new HashMap<>();
		for (String topic : topics) {
			kafkaWriters.put(topic, new KafkaSender(kafkaSynchronize, maxSize, topic, partitionKey));
		}
	}

	public void shutDown() {
		try {
			for (KafkaSender kafkaSender : kafkaWriters.values()){
				kafkaSender.shutDown();
			}
		} catch (Exception ex) {
			logger.error("Error while closing the kafka writer. Error {}", ex.getMessage());
		}
	}

	@Override public void callSynchronizer(long epochTime) {
		// Update the synchronizer with number of events
		// This implementation currently works only with ReachSumMetricsDecider
		// TODO: refctor to work with different deciders  
		kafkaSynchronize.synchronize(messagesCounter);

	}

	public void send(String topic, String messageStr, long epochTime) throws Exception{
		kafkaWriters.get(topic).send(messageStr, epochTime);
		messagesCounter++;

		if (messagesCounter == maxSize) {
			logger.info("{} messages sent, waiting for last message time {}", messagesCounter, epochTime);
			callSynchronizer(epochTime);
			messagesCounter = 0;
		}
	}
}
