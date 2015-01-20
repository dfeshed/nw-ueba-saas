package fortscale.utils.kafka;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

@Configurable(preConstruction=true)
public class KafkaEventsWriter implements Closeable {

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
	@Value("${kafka.queue.time:5000}")
	protected int queueTime;
	@Value("${kafka.queue.size:10000}")
	protected int queueSize;
	@Value("${kafka.batch.size:200}")
	protected int batchSize;
	
	private Producer<String, String> producer;
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
		if (producer==null) {
			// build kafka producer
			Properties props = new Properties();
			props.put("metadata.broker.list", kafkaBrokerList);
			props.put("serializer.class", serializer);
			props.put("partitioner.class", partitionerClass);
			props.put("request.required.acks", requiredAcks);
			props.put("producer.type", producerType);
			props.put("retry.backoff.ms", retryBackoff);
			props.put("queue.time", queueTime);
			props.put("queue.size", queueSize);
			props.put("batch.size", batchSize);

			ProducerConfig config = new ProducerConfig(props);

			producer = new Producer<String, String>(config);
		}
		return  producer;
	}

	
	public void send(String key, String data) {
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, key, data);
		getProducer().send(message);
	}
	
	
	@Override
	public void close() {
		if (producer!=null)
			producer.close();
	}

	
	
}
