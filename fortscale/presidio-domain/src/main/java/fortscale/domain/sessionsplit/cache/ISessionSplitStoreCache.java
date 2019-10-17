package fortscale.domain.sessionsplit.cache;

import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;


public interface ISessionSplitStoreCache {
    /**
     * Write sessionSplitKey and sessionSplitValue to the store
     *
     * @param sessionSplitTransformerKey
     * @param sessionSplitTransformerValue
     */
    void write(SessionSplitTransformerKey sessionSplitTransformerKey, SessionSplitTransformerValue sessionSplitTransformerValue);

    /**
     * Remove sessionSplitKey from the cache and redis
     *
     * @param sessionSplitTransformerKey
     */
    void remove(SessionSplitTransformerKey sessionSplitTransformerKey);

    /**
     * Read sessionSplitValue from the store
     * @param sessionSplitTransformerKey
     * @return sessionSplitValue
     */
    SessionSplitTransformerValue read(SessionSplitTransformerKey sessionSplitTransformerKey);

}
