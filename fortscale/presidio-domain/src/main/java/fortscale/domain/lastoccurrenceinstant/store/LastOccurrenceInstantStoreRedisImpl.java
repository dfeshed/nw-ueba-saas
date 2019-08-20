package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.common.general.Schema;
import org.apache.commons.lang3.Validate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * A Redis based implementation of {@link LastOccurrenceInstantStore}.
 *
 * @author Lior Govrin.
 */
public class LastOccurrenceInstantStoreRedisImpl implements LastOccurrenceInstantStore {
    private static final String COLLECTION_NAME_PREFIX = "last-occurrence-instant";

    private final HashOperations<String, String, Instant> hashOperations;

    public LastOccurrenceInstantStoreRedisImpl(RedisTemplate<String, Map<String, Instant>> redisTemplate) {
        hashOperations = Validate.notNull(redisTemplate, "redisTemplate cannot be null.").opsForHash();
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        return hashOperations.get(getCollectionName(schema, entityType), entityId);
    }

    @Override
    public Map<String, Instant> readAll(Schema schema, String entityType, Collection<String> entityIds) {
        List<Instant> lastOccurrenceInstants = hashOperations.multiGet(getCollectionName(schema, entityType), entityIds);
        return entityIds.stream().collect(toMap(identity(), entityId -> lastOccurrenceInstants.remove(0)));
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        hashOperations.put(getCollectionName(schema, entityType), entityId, lastOccurrenceInstant);
    }

    @Override
    public void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap) {
        hashOperations.putAll(getCollectionName(schema, entityType), entityIdToLastOccurrenceInstantMap);
    }

    @Override
    public void close() {}

    private static String getCollectionName(Schema schema, String entityType) {
        return String.format("%s:%s:%s", COLLECTION_NAME_PREFIX, schema.getName(), entityType);
    }
}
