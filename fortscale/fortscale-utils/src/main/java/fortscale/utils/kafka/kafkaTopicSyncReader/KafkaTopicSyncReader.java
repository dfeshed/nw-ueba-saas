package fortscale.utils.kafka.kafkaTopicSyncReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.AbstractKafkaTopicReader;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.kafka.metricMessageModels.MetricMessageAdditionalMetaData;
import fortscale.utils.logging.Logger;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * blocking\synced (working in the same thread) kafka topic reader
 */
@Configurable(preConstruction = true)
public class KafkaTopicSyncReader {
    private static final Logger logger = Logger.getLogger(KafkaTopicSyncReader.class);

    @Value("#{'${kafka.broker.list}'.split(':')}")
    private String[] hostAndPort;
    @Value("${fortscale.kafka.so.timeout:10000}")
    private int soTimeout;
    @Value("${fortscale.kafka.buffer.size:100000}")
    private int bufferSize;
    @Value("${fortscale.kafka.fetch.size:100000}")
    private int fetchSize;

    private long offset=0;


    public KafkaTopicSyncReader(int fetchSize,int bufferSize,int soTimeout,String[] hostAndPort) {
        this.fetchSize=fetchSize;
        this.bufferSize=bufferSize;
        this.soTimeout=soTimeout;
        this.hostAndPort=hostAndPort;
    }


    /**
     * reading messages from topic for clientid
     * @param consumer
     * @param clientId
     * @param topicName
     * @param partition
     * @return
     */
    private ByteBufferMessageSet getTopicMessageSet(SimpleConsumer consumer, String clientId,String topicName,int partition) {
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

    /**
     * read messages from topic
     * @return List<MetricMessageAdditionalMetaData> POJO filled with metrics
     */
    public List<MetricMessageAdditionalMetaData> getMessagesAsMetricMessage( String clientId, String topicName, int partition) {
        List<MetricMessageAdditionalMetaData> result = new ArrayList<>();
        SimpleConsumer simpleConsumer = null;
        try {
            simpleConsumer = new SimpleConsumer(
                    hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                    soTimeout, bufferSize, clientId);

            if(offset==0)
                offset = AbstractKafkaTopicReader.getLastOffset(clientId, topicName, partition, simpleConsumer);

            for (MessageAndOffset messageAndOffset : getTopicMessageSet(simpleConsumer,clientId,topicName,partition)) {
                if (messageAndOffset.message()!=null) {
                    MetricMessage message = convertMessageAndOffsetToMetricMessage(messageAndOffset);
                    if (message != null) {
                        MetricMessageAdditionalMetaData fullMessage = new MetricMessageAdditionalMetaData();
                        fullMessage.setMetricMessage(message);
                        fullMessage.setMetricMessageSize(messageAndOffset.message().size());
                        result.add(fullMessage);
                    }
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

    /**
     * converts MessageAndOffset object (kafka's topic message object )to MetricMessage POJO
     * @param messageAndOffset
     * @return MetricMessage object
     */
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
