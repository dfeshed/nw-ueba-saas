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

    private final ValueOperations<String, Object> valueOperations;
    private RedisTemplate<String, Object> redisTemplate;
    private final Duration timeout;

    public SessionSplitStoreRedis(RedisTemplate<String, Object> redisTemplate, Duration timeout) {
        valueOperations = Validate.notNull(redisTemplate, "redisTemplate cannot be null.").opsForValue();
        this.redisTemplate = redisTemplate;
        this.timeout = timeout;
    }

    /**
     * Write sessionSplitKey and sessionSplitValue to the store
     * @param sessionSplitTransformerKey
     * @param sessionSplitTransformerValue
     */
    public void write(SessionSplitTransformerKey sessionSplitTransformerKey, SessionSplitTransformerValue sessionSplitTransformerValue) {
        String key = getRedisKey(sessionSplitTransformerKey.getSrcIp(), sessionSplitTransformerKey.getDstIp(), sessionSplitTransformerKey.getSrcPort(), sessionSplitTransformerKey.getDstPort());
        valueOperations.set(key, sessionSplitTransformerValue);
    }


    /**
     * Write sessionpSplitMap to the store
     * @param splitTransformerMap
     */
    public void writeAll(Map<SessionSplitTransformerKey, SessionSplitTransformerValue> splitTransformerMap) {
        redisTemplate.executePipelined(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                splitTransformerMap.forEach((sessionSplitTransformerKey, sessionSplitTransformerValue) -> {
                    String key = getRedisKey(sessionSplitTransformerKey.getSrcIp(), sessionSplitTransformerKey.getDstIp(), sessionSplitTransformerKey.getSrcPort(), sessionSplitTransformerKey.getDstPort());
                    operations.opsForValue().set(key, sessionSplitTransformerValue, timeout);
                });
                return null;
            }
        });
    }

    /**
     * Read sessionSplitValue from the store
     * @param sessionSplitTransformerKey
     * @return sessionSplitValue
     */
    public SessionSplitTransformerValue read(SessionSplitTransformerKey sessionSplitTransformerKey) {
        String key = getRedisKey(sessionSplitTransformerKey.getSrcIp(), sessionSplitTransformerKey.getDstIp(), sessionSplitTransformerKey.getSrcPort(), sessionSplitTransformerKey.getDstPort());
        return (SessionSplitTransformerValue) valueOperations.get(key);
    }

    private static String getRedisKey(String srcIp, String dstIp, String srcPort, String dstPort) {
        return String.format("%s:%s:%s:%s:%s", REDIS_KEY_PREFIX, srcIp, dstIp, srcPort, dstPort);
    }


}
