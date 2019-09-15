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
 * A Redis based implementation of {@link ISessionSplitStore}.
 */
public class SessionSplitStoreRedisImpl implements ISessionSplitStore {
    private static final String REDIS_KEY_PREFIX = "session-split";

    private final ValueOperations<String, Object> valueOperations;
    private RedisTemplate<String, Object> redisTemplate;
    private final Duration timeout;

    public SessionSplitStoreRedisImpl(RedisTemplate<String, Object> redisTemplate, Duration timeout) {
        valueOperations = Validate.notNull(redisTemplate, "redisTemplate cannot be null.").opsForValue();
        this.redisTemplate = redisTemplate;
        this.timeout = timeout;
    }

    @Override
    public void write(SessionSplitTransformerKey sessionSplitTransformerKey, SessionSplitTransformerValue sessionSplitTransformerValue) {
        String key = getRedisKey(sessionSplitTransformerKey.getSrcIp(), sessionSplitTransformerKey.getDstIp(), sessionSplitTransformerKey.getSrcPort(), sessionSplitTransformerKey.getDstPort());
        valueOperations.set(key, sessionSplitTransformerValue);
    }


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

    @Override
    public SessionSplitTransformerValue read(SessionSplitTransformerKey sessionSplitTransformerKey) {
        String key = getRedisKey(sessionSplitTransformerKey.getSrcIp(), sessionSplitTransformerKey.getDstIp(), sessionSplitTransformerKey.getSrcPort(), sessionSplitTransformerKey.getDstPort());
        return (SessionSplitTransformerValue) valueOperations.get(key);
    }

    private static String getRedisKey(String srcIp, String dstIp, String srcPort, String dstPort) {
        return String.format("%s:%s:%s:%s:%s", REDIS_KEY_PREFIX, srcIp, dstIp, srcPort, dstPort);
    }


}
