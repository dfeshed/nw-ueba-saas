package fortscale.monitoring.external.stats.samza.collector.topicReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import fortscale.utils.logging.Logger;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import kafka.message.MessageAndOffset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * reads from metrics topic and returns metric object
 */
public class SamzaMetricsTopicSyncReader extends KafkaTopicSyncReader {

    private static final Logger logger = Logger.getLogger(SamzaMetricsTopicSyncReader.class);

    public SamzaMetricsTopicSyncReader(int fetchSize, int bufferSize, int soTimeout, String[] hostAndPort, String clientId, String topicName, int partition) {
        super(fetchSize, bufferSize, soTimeout, hostAndPort,clientId,topicName,partition);
    }

    /**
     * converts MessageAndOffset object (kafka's topic message object )to MetricMessage POJO
     *
     * @param messageAndOffset message and offset
     * @return MetricMessage object
     */
    public MetricMessage convertMessageAndOffsetToMetricMessage(MessageAndOffset messageAndOffset) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuffer byteBuffer = messageAndOffset.message().payload();
        if(logger.isDebugEnabled()) {
            logger.debug("converting message from offset: {}", messageAndOffset.offset());
        }
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        try {
            MetricMessage result = mapper.readValue(bytes, MetricMessage.class);
            if (logger.isDebugEnabled()) {
                logger.debug("converted message: {}", result.toString());
            }
            return result;
        } catch (IOException e) {
            String message = new String(bytes, StandardCharsets.UTF_8);
            String warningMsg = String.format("Failed to convert message to MetricMessage object from offset: %s. message content: %s", messageAndOffset.offset(),message);
            logger.warn(warningMsg,e);
            return null;
        }
    }

    /**
     * read messages from metrics topic
     *
     * @return List<SamzaMetricsTopicSyncReaderResponse> POJO filled with metrics
     */
    public SamzaMetricsTopicSyncReaderResponse getMessagesAsMetricMessages() {
        SamzaMetricsTopicSyncReaderResponse result = new SamzaMetricsTopicSyncReaderResponse();

        for (MessageAndOffset messageAndOffset : getByteBufferMessagesSet()) {
            if (messageAndOffset.message() == null) {
                continue;
            }

            MetricMessage message = convertMessageAndOffsetToMetricMessage(messageAndOffset);
            if (message != null) {
                result.addMetricMessages(message);
            }
            else {
                result.addUnresolvedMessages();
            }
        }

        return result;
    }
}
