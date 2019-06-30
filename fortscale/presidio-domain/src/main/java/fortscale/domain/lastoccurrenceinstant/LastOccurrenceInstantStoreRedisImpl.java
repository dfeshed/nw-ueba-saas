package fortscale.domain.lastoccurrenceinstant;

import fortscale.common.general.Schema;
import org.apache.commons.lang3.Validate;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Map;

/**
 * A Redis based implementation of {@link LastOccurrenceInstantReader} and {@link LastOccurrenceInstantWriter}.
 *
 * @author Lior Govrin.
 */
public class LastOccurrenceInstantStoreRedisImpl implements LastOccurrenceInstantReader, LastOccurrenceInstantWriter {
    private static final String COLLECTION_NAME_PREFIX = "last_occurrence_instant";

    private final HashOperations<String, String, Instant> hashOperations;

    public LastOccurrenceInstantStoreRedisImpl(JedisConnectionFactory jedisConnectionFactory) {
        Validate.notNull(jedisConnectionFactory, "jedisConnectionFactory cannot be null.");
        RedisTemplate<String, Map<String, Instant>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        return hashOperations.get(getCollectionName(schema, entityType), entityId);
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        hashOperations.put(getCollectionName(schema, entityType), entityId, lastOccurrenceInstant);
    }

    private static String getCollectionName(Schema schema, String entityType) {
        return String.format("%s_%s_%s", COLLECTION_NAME_PREFIX, schema.getName(), entityType);
    }
}
