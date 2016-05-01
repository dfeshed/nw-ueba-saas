package fortscale.utils.kafka.kafkaTopicSyncReader;

import fortscale.utils.kafka.AbstractKafkaTopicReader;
import fortscale.utils.logging.Logger;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;

/**
 * blocking\synced (working in the same thread) kafka topic reader
 */
public class KafkaTopicSyncReader {
    private static final Logger logger = Logger.getLogger(KafkaTopicSyncReader.class);

    protected String[] hostAndPort;
    protected int soTimeout;
    protected int bufferSize;
    private int fetchSize;

    protected long offset = 0;


    public KafkaTopicSyncReader(int fetchSize, int bufferSize, int soTimeout, String[] hostAndPort) {
        this.fetchSize = fetchSize;
        this.bufferSize = bufferSize;
        this.soTimeout = soTimeout;
        this.hostAndPort = hostAndPort;
    }


    /**
     * read messages from metrics
     *
     * @return List<KafkaTopicSyncReaderResponse> POJO filled with metrics
     */
    public ByteBufferMessageSet getByteBufferMessagesSet(String clientId, String topicName, int partition) {
        ByteBufferMessageSet result = null;
        SimpleConsumer simpleConsumer = null;
        try {
            simpleConsumer = new SimpleConsumer(
                    hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                    soTimeout, bufferSize, clientId);

            if (offset == 0) {
                offset = AbstractKafkaTopicReader.getLastOffset(clientId, topicName, partition, simpleConsumer);
            }
            logger.debug("EXECUTING: fetch from topic: {} partition: {}  clientId: {}, fetchSize: {} offset: {}",topicName,partition,clientId,fetchSize,offset);
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId(clientId)
                    .addFetch(topicName, partition, offset, fetchSize)
                    .build();
            FetchResponse fetchResponse = simpleConsumer.fetch(fetchRequest);
            logger.debug("Fetch response: {}",fetchResponse.toString());
            if (fetchResponse.hasError()) {
                logger.error("Failed to fetch messages from topic {}, partition {}. Error code: {}.",
                        topicName, partition, fetchResponse.errorCode(topicName, partition));
            }
            result = fetchResponse.messageSet(topicName, partition);
            result.forEach(messageAndOffset -> offset = messageAndOffset.nextOffset());

        } finally {
            if (simpleConsumer != null) {
                simpleConsumer.close();
            }
        }
        return result;
    }


}
