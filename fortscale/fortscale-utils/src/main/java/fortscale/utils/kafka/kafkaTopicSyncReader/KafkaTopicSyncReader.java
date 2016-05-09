package fortscale.utils.kafka.kafkaTopicSyncReader;

import fortscale.utils.kafka.AbstractKafkaTopicReader;
import fortscale.utils.kafka.kafkaTopicSyncReader.exceptions.KafkaFetchResponseException;
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
    protected int fetchSize;
    protected String clientId;
    protected String topicName;
    protected int partition;
    protected long offset = 0;
    protected SimpleConsumer simpleConsumer;


    /**
     * @param fetchSize   - fetch size in bytes
     * @param bufferSize  - buffer size in bytes
     * @param soTimeout   - socket timeout in milliseconds
     * @param hostAndPort - host and port array
     * @param clientId    - kafka reader client id
     * @param topicName   - kafka topic name to read from
     * @param partition   - kafka partition
     */
    public KafkaTopicSyncReader(int fetchSize, int bufferSize, int soTimeout, String[] hostAndPort, String clientId, String topicName, int partition) {
        this.fetchSize = fetchSize;
        this.bufferSize = bufferSize;
        this.soTimeout = soTimeout;
        this.hostAndPort = hostAndPort;
        this.clientId = clientId;
        this.topicName = topicName;
        this.partition = partition;
    }


    /**
     * read messages from metrics
     *
     * @return ByteBufferMessageSet filled with messages
     */
    public ByteBufferMessageSet getByteBufferMessagesSet() {
        ByteBufferMessageSet result = null;
        try {
            if (simpleConsumer == null) {
                simpleConsumer = new SimpleConsumer(
                        hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                        soTimeout, bufferSize, clientId);
            }

            if (offset == 0) {
                logger.info("getting last offset for clientId: {} topicName: {}, partition: {}, at host: {}:{}", clientId, topicName, partition, hostAndPort[0], hostAndPort[1]);
                offset = AbstractKafkaTopicReader.getLastOffset(clientId, topicName, partition, simpleConsumer);
            }
            logger.debug("executing fetch from topic: {} partition: {}  clientId: {}, fetchSize: {} offset: {} at host {}:{}", topicName, partition, clientId, fetchSize, offset, hostAndPort[0], hostAndPort[1]);
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId(clientId)
                    .addFetch(topicName, partition, offset, fetchSize)
                    .build();
            FetchResponse fetchResponse = simpleConsumer.fetch(fetchRequest);
            logger.debug("Fetch response: {}, response size: {}", fetchResponse.toString(), fetchResponse.toString().length());
            if (fetchResponse.hasError()) {
                KafkaFetchResponseException kafkaFetchResponseException = new KafkaFetchResponseException(topicName, partition, fetchResponse.errorCode(topicName, partition), hostAndPort[0], hostAndPort[1]);
                logger.error("error in fetch response", kafkaFetchResponseException);
                throw kafkaFetchResponseException;
            }
            result = fetchResponse.messageSet(topicName, partition);
            result.forEach(messageAndOffset -> offset = messageAndOffset.nextOffset());

        } catch (Exception e) {
            String errorMessage = String.format("error reading from kafka topic: %s clientid: %s, partition: %d, host: %s:%s, fetchSize: %d, bufferSize: %d, socket timeout: %d", topicName, clientId, partition, hostAndPort[0], hostAndPort[1], fetchSize, bufferSize, soTimeout);
            logger.error(errorMessage, e);
            close();
            throw e;

        }
        return result;
    }

    /**
     * closes simple consumer connetion
     */
    public void close() {
        logger.info("closing consumer topic: {} partition: {}  clientId: {}, fetchSize: {} offset: {} at host {}:{}", topicName, partition, clientId, fetchSize, offset, hostAndPort[0], hostAndPort[1]);
        simpleConsumer.close();
    }


}
