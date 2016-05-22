package fortscale.monitoring.metrics.adapter.topicReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.kafkaTopicSyncReader.KafkaTopicSyncReader;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import kafka.message.MessageAndOffset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * reads from metrics topic and returns EngineData metric object
 */
public class EngineDataTopicSyncReader extends KafkaTopicSyncReader {

    private static final Logger logger = Logger.getLogger(EngineDataTopicSyncReader.class);

    public EngineDataTopicSyncReader(int fetchSize, int bufferSize, int soTimeout, String[] hostAndPort, String clientId, String topicName, int partition) {
        super(fetchSize, bufferSize, soTimeout, hostAndPort,clientId,topicName,partition);
    }

    /**
     * converts MessageAndOffset object (kafka's topic message object )to EngineData POJO
     *
     * @param messageAndOffset
     * @return engine data message object
     */
    public EngineData convertMessageAndOffsetToEngineData(MessageAndOffset messageAndOffset) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuffer byteBuffer = messageAndOffset.message().payload();
        if(logger.isDebugEnabled()) {
            logger.debug("converting message from offset: {}", messageAndOffset.offset());
        }
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        try {
            EngineData result = mapper.readValue(bytes, EngineData.class);
            if (logger.isDebugEnabled()) {
                logger.debug("converted message: {}", result.toString());
            }
            return result;
        } catch (IOException e) {
            String message = new String(bytes, StandardCharsets.UTF_8);
            String warningMsg = String.format("Failed to convert message to EngineData object from offset: %s, message: %s.", messageAndOffset.offset(),message);
            logger.warn(warningMsg,e);
            return null;
        }
    }

    /**
     * read messages from metrics topic
     *
     * @return  POJO filled with metrics
     */
    public List<EngineDataTopicSyncReaderResponse> getMessagesAsEngineDataMetricMessages() {
        List<EngineDataTopicSyncReaderResponse> result = new ArrayList<>();

        for (MessageAndOffset messageAndOffset : getByteBufferMessagesSet()) {
            if (messageAndOffset.message() == null) {
                continue;
            }
            EngineData message = convertMessageAndOffsetToEngineData(messageAndOffset);
            EngineDataTopicSyncReaderResponse fullMessage = new EngineDataTopicSyncReaderResponse();
            if (message != null) {
                fullMessage.setMessage(message);
                result.add(fullMessage);
            }
            else {
                fullMessage.addUnresolvedMessages();
            }
        }

        return result;
    }
}
