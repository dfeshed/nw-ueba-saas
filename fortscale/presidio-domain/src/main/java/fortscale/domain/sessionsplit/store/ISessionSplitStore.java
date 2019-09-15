package fortscale.domain.sessionsplit.store;

import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;

import java.util.Map;

public interface ISessionSplitStore {
    /**
     * Write sessionSplitKey and sessionSplitValue to the store
     * @param sessionSplitKey
     * @param sessionSplitValue
     */
    void write(SessionSplitTransformerKey sessionSplitKey, SessionSplitTransformerValue sessionSplitValue);

    /**
     * Write sessionpSplitMap to the store
     * @param sessionpSplitMap
     */
    void writeAll(Map<SessionSplitTransformerKey, SessionSplitTransformerValue> sessionpSplitMap);


    /**
     * Read sessionSplitValue from the store
     * @param sessionSplitTransformerKey
     * @return sessionSplitValue
     */
    SessionSplitTransformerValue read(SessionSplitTransformerKey sessionSplitTransformerKey);


}
