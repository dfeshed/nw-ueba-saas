package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A Redis based implementation of {@link LastOccurrenceInstantStore}.
 *
 * @author Lior Govrin.
 */
public class LastOccurrenceInstantStoreRedisImpl implements LastOccurrenceInstantStore {
    private static final Logger logger = Logger.getLogger(LastOccurrenceInstantStoreRedisImpl.class);
    private static final String REDIS_KEY_PREFIX = "last-occurrence-instant";

    private final RedisTemplate<String, Instant> redisTemplate;
    private final ValueOperations<String, Instant> valueOperations;
    private final Duration timeout;

    public LastOccurrenceInstantStoreRedisImpl(RedisTemplate<String, Instant> redisTemplate, Duration timeout) {
        this.redisTemplate = Validate.notNull(redisTemplate, "redisTemplate cannot be null.");
        this.valueOperations = redisTemplate.opsForValue();
        this.timeout = Validate.notNull(timeout, "timeout cannot be null.");
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        return valueOperations.get(getRedisKey(schema, entityType, entityId));
    }

    @Override
    public Map<String, Instant> readAll(Schema schema, String entityType, List<String> entityIds) {
        List<Instant> lastOccurrenceInstants = valueOperations.multiGet(entityIds);
        int numberOfEntityIds = entityIds.size();

        if (lastOccurrenceInstants == null) {
            String format = "multiGet returned a null list of last occurrence instants for {} entity ID(s).";
            logger.error(format, numberOfEntityIds);
            return Collections.emptyMap();
        }

        int numberOfLastOccurrenceInstants = lastOccurrenceInstants.size();

        if (numberOfLastOccurrenceInstants != numberOfEntityIds) {
            String format = "multiGet returned {} last occurrence instant(s) for {} entity ID(s).";
            logger.error(format, numberOfLastOccurrenceInstants, numberOfEntityIds);
            return Collections.emptyMap();
        }

        return IntStream.range(0, numberOfEntityIds).boxed()
                .collect(Collectors.toMap(entityIds::get, lastOccurrenceInstants::get));
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        valueOperations.set(getRedisKey(schema, entityType, entityId), lastOccurrenceInstant, timeout);
    }

    @Override
    public void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Object> execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                entityIdToLastOccurrenceInstantMap.forEach((entityId, lastOccurrenceInstant) ->
                        redisOperations.opsForValue().set(entityId, lastOccurrenceInstant, timeout));
                return redisOperations.exec();
            }
        });
    }

    @Override
    public void close() {}

    private static String getRedisKey(Schema schema, String entityType, String entityId) {
        return String.format("%s:%s:%s:%s", REDIS_KEY_PREFIX, schema.getName(), entityType, entityId);
    }
}
