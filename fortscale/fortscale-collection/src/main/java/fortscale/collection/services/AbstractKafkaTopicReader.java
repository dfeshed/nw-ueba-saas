package fortscale.collection.services;

import fortscale.utils.logging.Logger;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;

/**
 * An abstract service that reads from a specific partition of a given Kafka topic.
 * Extending classes should parse the incoming messages (in the form of JSON objects)
 * and save the relevant information.
 */
@Configurable(preConstruction = true)
public abstract class AbstractKafkaTopicReader {
	private static final Logger logger = Logger.getLogger(AbstractKafkaTopicReader.class);

	@Value("${fortscale.kafka.millis.to.sleep.between.fetch.requests}")
	private long millisToSleepBetweenFetchRequests;

	private Thread thread;
	private volatile boolean isRunning;

	/**
	 * @param host       the host name.
	 * @param port       the port number.
	 * @param soTimeout  the timeout in milliseconds.
	 * @param bufferSize the buffer size in bytes.
	 * @param clientId   the client ID.
	 * @param topic      the topic name.
	 * @param partition  the partition number of the topic.
	 * @param fetchSize  the fetch size in bytes.
	 */
	public AbstractKafkaTopicReader(
			String host, int port, int soTimeout, int bufferSize,
			String clientId, String topic, int partition, int fetchSize) {

		Assert.hasText(host);
		Assert.isTrue(port >= 0);
		Assert.isTrue(soTimeout > 0);
		Assert.isTrue(bufferSize > 0);
		Assert.hasText(clientId);
		Assert.hasText(topic);
		Assert.isTrue(partition >= 0);
		Assert.isTrue(fetchSize > 0);

		thread = new Thread(() -> {
			run(host, port, soTimeout, bufferSize, clientId, topic, partition, fetchSize);
		});
	}

	/**
	 * Begins execution of this reader - Inner implementation calls the start() method
	 * of the thread running this reader. It is illegal to start the reader more than once
	 * (Even if it's after calling the end() method).
	 *
	 * @throws IllegalThreadStateException if the reader was already started.
	 */
	public void start() {
		isRunning = true;
		thread.start();
	}

	/**
	 * Stops execution of this reader - Inner implementation calls the join() method
	 * of the thread running this reader (which waits for the thread to die).
	 * This method should be called after the start() method.
	 */
	@SuppressWarnings("EmptyCatchBlock")
	public void end() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {}
	}

	/**
	 * Parses an incoming message and saves any necessary information.
	 * The implementing method is aware of the message's structure.
	 *
	 * @param message the incoming message in the form of a JSON object.
	 */
	protected abstract void handleMessage(JSONObject message);

	private void run(
			String host, int port, int soTimeout, int bufferSize,
			String clientId, String topic, int partition, int fetchSize) {

		SimpleConsumer simpleConsumer = null;
		long offset = 0;

		try {
			simpleConsumer = new SimpleConsumer(host, port, soTimeout, bufferSize, clientId);

			while (isRunning) {
				FetchRequest fetchRequest = new FetchRequestBuilder()
						.clientId(clientId)
						.addFetch(topic, partition, offset, fetchSize)
						.build();
				FetchResponse fetchResponse = simpleConsumer.fetch(fetchRequest);

				if (fetchResponse.hasError()) {
					logger.error("Failed to fetch messages from topic {}, partition {}. Error code: {}.",
							topic, partition, fetchResponse.errorCode(topic, partition));
				} else {
					for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(topic, partition)) {
						JSONObject message = getMessage(messageAndOffset);
						if (message != null) handleMessage(message);
						offset = messageAndOffset.nextOffset();
					}
				}

				try {
					Thread.sleep(millisToSleepBetweenFetchRequests);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		} finally {
			if (simpleConsumer != null) {
				simpleConsumer.close();
			}
		}
	}

	private static JSONObject getMessage(MessageAndOffset messageAndOffset) {
		ByteBuffer byteBuffer = messageAndOffset.message().payload();
		byte[] bytes = new byte[byteBuffer.limit()];
		byteBuffer.get(bytes);
		String source = new String(bytes);

		try {
			return new JSONObject(source);
		} catch (JSONException e) {
			logger.error("Failed to convert message to JSON object: {}. Exception message: {}.",
					source, e.getMessage());
			return null;
		}
	}
}
