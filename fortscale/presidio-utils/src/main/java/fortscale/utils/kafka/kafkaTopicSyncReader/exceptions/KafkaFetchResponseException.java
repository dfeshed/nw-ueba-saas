package fortscale.utils.kafka.kafkaTopicSyncReader.exceptions;

/**
 * Created by cloudera on 5/9/16.
 */
public class KafkaFetchResponseException extends RuntimeException{
    public KafkaFetchResponseException(String topicName, int partition, int errorCode, String host, String port) {
        super(String.format("ERROR: Failed to fetch messages from topic %s, partition %d. Error code: %d host: %s:%s",topicName,partition,errorCode,host,port));
    }
}
