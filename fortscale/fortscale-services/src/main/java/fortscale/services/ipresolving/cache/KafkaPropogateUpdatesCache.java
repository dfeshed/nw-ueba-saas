package fortscale.services.ipresolving.cache;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.utils.kafka.KafkaEventsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache implementation wrapper that sends all updates to a kafka topic
 */
public class KafkaPropogateUpdatesCache<T> implements  ResolvingCache<T> {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPropogateUpdatesCache.class);

    private ResolvingCache<T> innerCache;
    private KafkaEventsWriter topicWriter;

    // json serializer to serialize cache values to json when written to the kafka topic
    private ObjectMapper mapper;


    /**
     * Construct a new instance of the KafkaPropogateUpdatesCache that will forward all method requests to the
     * underlying given innerCache and send all updates on the kafka topic writer given.
     */
    public KafkaPropogateUpdatesCache(ResolvingCache<T> innerCache, KafkaEventsWriter topicWriter) {
        checkNotNull(innerCache);
        checkNotNull(topicWriter);
        this.innerCache = innerCache;
        this.topicWriter = topicWriter;

        // create json serializer for values
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    public T get(String ip) {
        return innerCache.get(ip);
    }

    public void put(String ip, T event) {
        innerCache.put(ip, event);
        try {
            topicWriter.send(ip, mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            logger.error("error serializing to json cache event " + event, e);
            throw new RuntimeException("error serializing to json cache event", e);
        }
    }
}
