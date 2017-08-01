package presidio.ade.domain.store.accumulator;

import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;


public interface AccumulatedDataStore {
    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<? extends AccumulatedAggregationFeatureRecord> records);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
