package fortscale.monitoring.samza.topicReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import fortscale.utils.logging.Logger;
import kafka.message.MessageAndOffset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
     * @param messageAndOffset
     * @return MetricMessage object
     */
    public MetricMessage convertMessageAndOffsetToMetricMessage(MessageAndOffset messageAndOffset) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuffer byteBuffer = messageAndOffset.message().payload();
        if(logger.isDebugEnabled()) {
            logger.debug("converting message: {}", messageAndOffset.message().toString());
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
            String warningMsg = String.format("Failed to convert message to MetricMessage object: %s.", messageAndOffset.message());
            logger.warn(warningMsg,e);
            return null;
        }
    }

    /**
     * read messages from metrics topic
     *
     * @return List<SamzaMetricsTopicSyncReaderResponse> POJO filled with metrics
     */
    public List<SamzaMetricsTopicSyncReaderResponse> getMessagesAsMetricMessages() {
        List<SamzaMetricsTopicSyncReaderResponse> result = new ArrayList<>();

        for (MessageAndOffset messageAndOffset : getByteBufferMessagesSet()) {
            if (messageAndOffset.message() == null) {
                continue;
            }

            MetricMessage message = convertMessageAndOffsetToMetricMessage(messageAndOffset);
            SamzaMetricsTopicSyncReaderResponse fullMessage = new SamzaMetricsTopicSyncReaderResponse();
            if (message != null) {

                fullMessage = new SamzaMetricsTopicSyncReaderResponse();
                fullMessage.setMetricMessage(message);
            }
            else {
                fullMessage.addUnresolvedMessages();
            }
            result.add(fullMessage);
        }

        return result;
    }
}
