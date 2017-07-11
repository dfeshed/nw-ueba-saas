package presidio.ade.domain.store.aggr;

import presidio.ade.domain.record.aggregated.AdeAggrRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * Created by barak_schuster on 7/10/17.
 */
public interface AggrDataStore {
    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<? extends AdeAggrRecord> records);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
