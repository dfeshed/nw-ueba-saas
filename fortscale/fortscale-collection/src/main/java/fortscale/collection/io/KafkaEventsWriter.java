package fortscale.collection.io;

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
	private String kafkaBrokerList;
	@Value("${kafka.requiredAcks:1}")
	private String requiredAcks;
	@Value("${kafka.producer.type:sync}")
	private String producerType;
	@Value("${kafka.serializer.class:kafka.serializer.StringEncoder}")
	private String serializer;
	@Value("${kafka.partitioner.class:kafka.producer.DefaultPartitioner}")
	private String partitionerClass;
	
	
	private Producer<String, String> producer;
	private String topic;
	
	public KafkaEventsWriter(String topic) {
		checkNotNull(topic);
		this.topic = topic;
		
		// build kafka producer
		Properties props = new Properties();
		props.put("metadata.broker.list", kafkaBrokerList);
		props.put("serializer.class", serializer);
		props.put("partitioner.class", partitionerClass);
		props.put("request.required.acks", requiredAcks);
		props.put("producer.type", producerType);
		ProducerConfig config = new ProducerConfig(props);
		
		producer = new Producer<String, String>(config);
	}
	
	
	public void send(String data) {
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, data);
		producer.send(message);
	}
	
	
	@Override
	public void close() {
		if (producer!=null)
			producer.close();
	}

	
	
}
