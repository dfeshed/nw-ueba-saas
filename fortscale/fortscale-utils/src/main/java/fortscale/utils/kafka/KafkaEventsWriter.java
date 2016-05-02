package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.Closeable;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

@Configurable(preConstruction=true)
/**
 * Thread-safe implementation of kafka events writer
 */
public class KafkaEventsWriter implements Closeable {
	private static final Logger logger = Logger.getLogger(KafkaEventsWriter.class);

	@Value("${kafka.broker.list}")
	protected String kafkaBrokerList;
	@Value("${kafka.requiredAcks:1}")
	protected String requiredAcks;
	@Value("${kafka.producer.type:async}")
	protected String producerType;
	@Value("${kafka.serializer.class:kafka.serializer.StringEncoder}")
	protected String serializer;
	@Value("${kafka.partitioner.class:fortscale.utils.kafka.partitions.StringHashPartitioner}")
	protected String partitionerClass;
	@Value("${kafka.partitioner.retry.backoff.ms:10000}")
	protected String retryBackoff;

	private volatile Producer<String, String> producer;

	private String topic;

	public KafkaEventsWriter(String topic) {
		checkNotNull(topic);
		this.topic = topic;
	}

	/**
	 * Ensure a producer is initialized and return it to caller. We initialize the producer upon call instead of
	 * in the class constructor since properties are not injected prior to constructor by spring using bean xml
	 * definition (as opposed to aspecj creation using new).
	 */
	private Producer<String, String> getProducer() {
		// we use double-checked locking to provide: 1.thread-safety 2. reduce performance overhead of the lock
		if (producer==null) {
			synchronized (this) {
				if (producer==null) {
					// build kafka producer
					Properties props = new Properties();
					props.put("metadata.broker.list", kafkaBrokerList);
					props.put("serializer.class", serializer);
					props.put("partitioner.class", partitionerClass);
					props.put("request.required.acks", requiredAcks);
					props.put("producer.type", producerType);
					props.put("retry.backoff.ms", retryBackoff);

					logger.debug("Creating KafkaEventsWriter for topic {} with properties {}", this.topic, props.toString());

					ProducerConfig config = new ProducerConfig(props);

					producer = new Producer<>(config);
				}
			}
		}
		return  producer;
	}

	public void send(String key, String data) {
		KeyedMessage<String, String> message = new KeyedMessage<>(topic, key, data);

		// kafka producer is thread-safe
		getProducer().send(message);
	}


	@Override
	public void close() {
		// using thread-safe manner, similarly to getProducer initialization method
		if (producer != null) {
			synchronized (this) {
				if (producer != null)
					producer.close();
			}
		}
	}
}
