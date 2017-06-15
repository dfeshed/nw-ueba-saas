package presidio.ade.domain.store.scored;

import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public interface ScoredEnrichedDataStore {

    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<? extends AdeScoredEnrichedRecord> records);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
