package fortscale.domain.sessionsplit.cache;

import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;
import fortscale.domain.sessionsplit.store.SessionSplitStoreRedis;
import fortscale.utils.flushable.AbstractFlushable;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;


public class SessionSplitStoreCacheImpl extends AbstractFlushable implements ISessionSplitStoreCache {
    private final SessionSplitStoreRedis sessionSplitStore;
    private final int maximumSize;
    private Map<SessionSplitTransformerKey, SessionSplitTransformerValue> splitTransformerMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param sessionSplitStore The underlying store to which information is written.
     * @param maximumSize       The maximum number of entries of this cache.
     */
    public SessionSplitStoreCacheImpl(
            SessionSplitStoreRedis sessionSplitStore,
            int maximumSize) {

        Validate.notNull(sessionSplitStore, "sessionSplitStore cannot be null.");
        Validate.isTrue(maximumSize > 0, "maximumSize must be greater than zero but is %d.", maximumSize);
        this.sessionSplitStore = sessionSplitStore;
        this.maximumSize = maximumSize;
    }

    @Override
    public void write(SessionSplitTransformerKey key, SessionSplitTransformerValue value) {
        if (splitTransformerMap.size() == maximumSize && !splitTransformerMap.containsKey(key)) {
            flush();
        }
        splitTransformerMap.put(key, value);
    }

    @Override
    public SessionSplitTransformerValue read(SessionSplitTransformerKey key) {
        SessionSplitTransformerValue value = splitTransformerMap.get(key);
        if (value == null) {
            value = sessionSplitStore.read(key);
            if (value != null) {
                if (splitTransformerMap.size() == maximumSize) {
                    flush();
                }
                splitTransformerMap.put(key, value);
            }
        }
        return value;
    }

    public void flush() {
        sessionSplitStore.writeAll(splitTransformerMap);
        splitTransformerMap.clear();
    }

}
