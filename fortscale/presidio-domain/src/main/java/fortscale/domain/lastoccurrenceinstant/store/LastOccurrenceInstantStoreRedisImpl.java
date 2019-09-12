package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.common.general.Schema;
import org.apache.commons.lang3.Validate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Redis based implementation of {@link LastOccurrenceInstantStore}.
 *
 * @author Lior Govrin.
 */
public class LastOccurrenceInstantStoreRedisImpl implements LastOccurrenceInstantStore {
    private static final String REDIS_KEY_PREFIX = "last-occurrence-instant";

    private final RedisTemplate<String, Instant> redisTemplate;
    private final ValueOperations<String, Instant> valueOperations;
    private final RedisSerializer<String> stringRedisSerializer;
    private final RedisSerializer<Instant> instantRedisSerializer;
    private final Duration timeout;

    @SuppressWarnings("unchecked")
    public LastOccurrenceInstantStoreRedisImpl(RedisTemplate<String, Instant> redisTemplate, Duration timeout) {
        this.redisTemplate = Validate.notNull(redisTemplate, "redisTemplate cannot be null.");
        this.valueOperations = redisTemplate.opsForValue();
        this.stringRedisSerializer = (RedisSerializer<String>)redisTemplate.getKeySerializer();
        this.instantRedisSerializer = (RedisSerializer<Instant>)redisTemplate.getValueSerializer();
        this.timeout = Validate.notNull(timeout, "timeout cannot be null.");
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        return valueOperations.get(getRedisKey(schema, entityType, entityId));
    }

    @Override
    public Map<String, Instant> readAll(Schema schema, String entityType, List<String> entityIds) {
        Map<String, Instant> entityIdToLastOccurrenceInstantMap = new HashMap<>();
        redisTemplate.executePipelined(action(redisConnection -> {
            for (String entityId : entityIds) {
                byte[] key = stringRedisSerializer.serialize(getRedisKey(schema, entityType, entityId));
                byte[] value = key == null ? null : redisConnection.get(key);
                Instant lastOccurrenceInstant = value == null ? null : instantRedisSerializer.deserialize(value);
                entityIdToLastOccurrenceInstantMap.put(entityId, lastOccurrenceInstant);
            }
        }));
        return entityIdToLastOccurrenceInstantMap;
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        valueOperations.set(getRedisKey(schema, entityType, entityId), lastOccurrenceInstant, timeout);
    }

    @Override
    public void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap) {
        redisTemplate.executePipelined(action(redisConnection -> {
            for (Map.Entry<String, Instant> mapEntry : entityIdToLastOccurrenceInstantMap.entrySet()) {
                byte[] key = stringRedisSerializer.serialize(getRedisKey(schema, entityType, mapEntry.getKey()));
                byte[] value = instantRedisSerializer.serialize(mapEntry.getValue());
                if (key != null)
                    // noinspection ConstantConditions - value can be null.
                    redisConnection.setEx(key, timeout.getSeconds(), value);
            }
        }));
    }

    @Override
    public void close() {}

    private static String getRedisKey(Schema schema, String entityType, String entityId) {
        return String.format("%s:%s:%s:%s", REDIS_KEY_PREFIX, schema.getName(), entityType, entityId);
    }

    private static RedisCallback<?> action(Consumer<RedisConnection> redisConnectionConsumer) {
        return redisConnection -> {
            redisConnectionConsumer.accept(redisConnection);
            return null;
        };
    }
}
