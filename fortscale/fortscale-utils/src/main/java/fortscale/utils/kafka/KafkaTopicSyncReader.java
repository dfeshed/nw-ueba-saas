package fortscale.utils.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


@Configurable(preConstruction = true)
public class KafkaTopicSyncReader {
    private static final Logger logger = Logger.getLogger(KafkaTopicSyncReader.class);

    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.kafka.so.timeout:10000}")
    private int soTimeout;
    @Value("${fortscale.kafka.buffer.size:1024000}")
    private int bufferSize;
    @Value("${fortscale.kafka.fetch.size:1024000}")
    private int fetchSize;

    private String clientId;
    private String topicName;
    private int partition;
    private long offset;

    public KafkaTopicSyncReader(String clientId, String topicName, int partition) {
        Assert.hasText(clientId);
        Assert.hasText(topicName);
        Assert.isTrue(partition >= 0);
        this.clientId = clientId;
        this.topicName = topicName;
        this.partition = partition;
    }

    private ByteBufferMessageSet getTopicMessageSet(SimpleConsumer consumer) {
        FetchRequest fetchRequest = new FetchRequestBuilder()
                .clientId(clientId)
                .addFetch(topicName, partition, offset, fetchSize)
                .build();
        FetchResponse fetchResponse = consumer.fetch(fetchRequest);
        if (fetchResponse.hasError()) {
            logger.error("Failed to fetch messages from topic {}, partition {}. Error code: {}.",
                    topicName, partition, fetchResponse.errorCode(topicName, partition));
        }
        return fetchResponse.messageSet(topicName, partition);
    }

    public List<MetricMessage> getMessagesAsMetricMessage() {
        List<MetricMessage> result = new ArrayList<>();
        SimpleConsumer simpleConsumer = null;
        try {
            simpleConsumer = new SimpleConsumer(
                    hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                    soTimeout, bufferSize, clientId);
            offset = AbstractKafkaTopicReader.getLastOffset(clientId, topicName, partition, simpleConsumer);

            for (MessageAndOffset messageAndOffset : getTopicMessageSet(simpleConsumer)) {
                if (messageAndOffset.message()!=null) {
                    MetricMessage message = convertMessageAndOffsetToMetricMessage(messageAndOffset);
                    if (message != null)
                        result.add(message);
                }
                offset = messageAndOffset.nextOffset();
            }
        } finally {
            if (simpleConsumer != null) {
                simpleConsumer.close();
            }
        }
        return result;
    }

    public static MetricMessage convertMessageAndOffsetToMetricMessage(MessageAndOffset messageAndOffset) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuffer byteBuffer = messageAndOffset.message().payload();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        try {
            MetricMessage result = mapper.readValue(bytes, MetricMessage.class);
            return result;
        } catch (IOException e) {
            logger.error("Failed to convert message to MetricMessage object: {}. Exception message: {}.",
                    messageAndOffset.message(), e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
