package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.common.general.Schema;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A Redis based implementation of {@link LastOccurrenceInstantStore}.
 *
 * @author Lior Govrin.
 */
public class LastOccurrenceInstantStoreRedisImpl implements LastOccurrenceInstantStore {
    private static final String REDIS_KEY_PREFIX = "last-occurrence-instant";

    private final ValueOperations<String, Instant> valueOperations;

    public LastOccurrenceInstantStoreRedisImpl(RedisTemplate<String, Instant> redisTemplate) {
        valueOperations = notNull(redisTemplate, "redisTemplate cannot be null.").opsForValue();
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        return valueOperations.get(getRedisKey(schema, entityType, entityId));
    }

    @Override
    public Map<String, Instant> readAll(Schema schema, String entityType, List<String> entityIds) {
        List<Instant> lastOccurrenceInstants = valueOperations.multiGet(getRedisKeys(schema, entityType, entityIds));
        return lastOccurrenceInstants == null ? emptyMap() : entityIds.stream().collect(toMap(
                identity(),
                entityId -> lastOccurrenceInstants.remove(0)
        ));
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        valueOperations.set(getRedisKey(schema, entityType, entityId), lastOccurrenceInstant);
    }

    @Override
    public void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap) {
        valueOperations.multiSet(entityIdToLastOccurrenceInstantMap.entrySet().stream().collect(toMap(
                entry -> getRedisKey(schema, entityType, entry.getKey()),
                Entry::getValue
        )));
    }

    @Override
    public void close() {}

    private static String getRedisKey(Schema schema, String entityType, String entityId) {
        return String.format("%s:%s:%s:%s", REDIS_KEY_PREFIX, schema.getName(), entityType, entityId);
    }

    private static List<String> getRedisKeys(Schema schema, String entityType, List<String> entityIds) {
        return entityIds.stream().map(entityId -> getRedisKey(schema, entityType, entityId)).collect(toList());
    }
}
