package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public interface ScoredDataStore {

    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<? extends AdeScoredRecord> records);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
