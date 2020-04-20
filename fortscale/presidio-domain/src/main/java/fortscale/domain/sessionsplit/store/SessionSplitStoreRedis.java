package fortscale.domain.sessionsplit.store;

import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;
import org.apache.commons.lang3.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * SessionSplit Redis based implementation
 */
public class SessionSplitStoreRedis {
    private static final String REDIS_KEY_PREFIX = "session-split";
    private static final String REDIS_KEY_DELIMITER = ":";

    private final ValueOperations<String, Object> valueOperations;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Duration timeout;

    public SessionSplitStoreRedis(RedisTemplate<String, Object> redisTemplate, Duration timeout) {
        valueOperations = Validate.notNull(redisTemplate, "redisTemplate cannot be null.").opsForValue();
        this.redisTemplate = redisTemplate;
        this.timeout = timeout;
    }

    /**
     * Write sessionSplitKey and sessionSplitValue to the store
     * @param sessionSplitTransformerKey   key
     * @param sessionSplitTransformerValue value
     */
    public void write(SessionSplitTransformerKey sessionSplitTransformerKey, SessionSplitTransformerValue sessionSplitTransformerValue) {
        String key = getRedisKey(sessionSplitTransformerKey);
        valueOperations.set(key, sessionSplitTransformerValue, timeout);
    }

    /**
     * Write sessionSplitMap to the store
     * @param splitTransformerMap key-value map
     */
    public void writeAll(Map<SessionSplitTransformerKey, SessionSplitTransformerValue> splitTransformerMap) {
        redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                splitTransformerMap.forEach((sessionSplitTransformerKey, sessionSplitTransformerValue) -> {
                    String key = getRedisKey(sessionSplitTransformerKey);
                    operations.opsForValue().set(key, sessionSplitTransformerValue, timeout);
                });
                operations.exec();
                return null;
            }
        });
    }

    /**
     * Read sessionSplitValue from the store
     * @param sessionSplitTransformerKey key
     * @return sessionSplitValue
     */
    public SessionSplitTransformerValue read(SessionSplitTransformerKey sessionSplitTransformerKey) {
        String key = getRedisKey(sessionSplitTransformerKey);
        return (SessionSplitTransformerValue) valueOperations.get(key);
    }

    /**
     * Remove sessionSplitKey from the store
     * @param sessionSplitTransformerKey key
     */
    public void remove(SessionSplitTransformerKey sessionSplitTransformerKey) {
        String key = getRedisKey(sessionSplitTransformerKey);
        valueOperations.getOperations().delete(key);
    }

    private static String getRedisKey(SessionSplitTransformerKey sessionSplitTransformerKey) {
        // noinspection StringBufferReplaceableByString - Use StringBuilder instead of String.format.
        return new StringBuilder(REDIS_KEY_PREFIX).append(REDIS_KEY_DELIMITER)
                .append(sessionSplitTransformerKey.getSrcIp()).append(REDIS_KEY_DELIMITER)
                .append(sessionSplitTransformerKey.getDstIp()).append(REDIS_KEY_DELIMITER)
                .append(sessionSplitTransformerKey.getSrcPort()).append(REDIS_KEY_DELIMITER)
                .append(sessionSplitTransformerKey.getDstPort()).toString();
    }
}
