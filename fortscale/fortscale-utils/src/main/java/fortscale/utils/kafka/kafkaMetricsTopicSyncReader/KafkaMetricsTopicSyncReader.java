package fortscale.utils.kafka.kafkaMetricsTopicSyncReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import fortscale.utils.kafka.metricMessageModels.KafkaTopicSyncReaderResponse;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import kafka.message.MessageAndOffset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * reads from metrics topic and returns metric object
 */
public class KafkaMetricsTopicSyncReader extends KafkaTopicSyncReader {

    private final String METRICS_TOPIC_NAME = "metrics";
    private static final Logger logger = Logger.getLogger(KafkaMetricsTopicSyncReader.class);


    public KafkaMetricsTopicSyncReader(int fetchSize, int bufferSize, int soTimeout, String[] hostAndPort) {
        super(fetchSize, bufferSize, soTimeout, hostAndPort);
    }

    /**
     * converts MessageAndOffset object (kafka's topic message object )to MetricMessage POJO
     *
     * @param messageAndOffset
     * @return MetricMessage object
     */
    public static MetricMessage convertMessageAndOffsetToMetricMessage(MessageAndOffset messageAndOffset) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuffer byteBuffer = messageAndOffset.message().payload();
        logger.debug("converting message: {}", messageAndOffset.message().toString());
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        try {
            MetricMessage result = mapper.readValue(bytes, MetricMessage.class);
            logger.debug("converted message: {}", result.toString());
            return result;
        } catch (IOException e) {
            logger.error("Failed to convert message to MetricMessage object: {}. Exception message: {}.",
                    messageAndOffset.message(), e.getMessage());
            return null;
        }
    }

    /**
     * read messages from metrics topic
     *
     * @return List<KafkaTopicSyncReaderResponse> POJO filled with metrics
     */
    public List<KafkaTopicSyncReaderResponse> getMessagesAsMetricMessage(String clientId, int partition) {
        List<KafkaTopicSyncReaderResponse> result = new ArrayList<>();

        for (MessageAndOffset messageAndOffset : getByteBufferMessagesSet(clientId, METRICS_TOPIC_NAME, partition)) {
            if (messageAndOffset.message() != null) {
                MetricMessage message = convertMessageAndOffsetToMetricMessage(messageAndOffset);
                if (message != null) {

                    KafkaTopicSyncReaderResponse fullMessage = new KafkaTopicSyncReaderResponse();
                    fullMessage.setMetricMessage(message);
                    fullMessage.setMetricMessageSize(messageAndOffset.message().size());
                    result.add(fullMessage);
                }
            }
        }

        return result;
    }
}
