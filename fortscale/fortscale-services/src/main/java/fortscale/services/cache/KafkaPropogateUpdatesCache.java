package fortscale.services.cache;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import fortscale.utils.kafka.KafkaEventsWriter;

/**
 * Cache implementation wrapper that sends all updates to a kafka topic
 */
public class KafkaPropogateUpdatesCache<K,T> extends CacheHandler<K,T> {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPropogateUpdatesCache.class);

    private CacheHandler<K,T> innerCache;
    private KafkaEventsWriter topicWriter;


    /**
     * Construct a new instance of the KafkaPropogateUpdatesCache that will forward all method requests to the
     * underlying given innerCache and send all updates on the kafka topic writer given.
     */
    public KafkaPropogateUpdatesCache(CacheHandler<K,T> innerCache, KafkaEventsWriter topicWriter, Class<T> clazz) {
        super(clazz);
        checkNotNull(innerCache);
        checkNotNull(topicWriter);
        this.innerCache = innerCache;
        this.topicWriter = topicWriter;
    }

    public T get(K key) {
        return innerCache.get(key);
    }

    public void put(K key, T value) {
        innerCache.put(key, value);
        try {
            topicWriter.send(keyAsString(key), mapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            logger.error("error serializing to json cache value " + value, e);
            throw new RuntimeException("error serializing to json cache value", e);
        }
    }

    @Override public void remove(K key) {
        innerCache.remove(key);
        topicWriter.send(keyAsString(key),null);
    }

    @Override
    public void clear() {
        innerCache.clear();
    }

    @Override
    public Map<K, T> getAll() {
        return innerCache.getAll();
    }

    protected String keyAsString(K key){
        return key.toString();
    }

    @Override
    public void close() throws IOException {
        innerCache.close();
        topicWriter.close();
    }
}
