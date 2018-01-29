package presidio.ade.domain.store.accumulator;

import fortscale.utils.store.record.StoreMetadataProperties;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;


public interface AggregationEventsAccumulationDataStore extends AggregationEventsAccumulationDataReader {
    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<AccumulatedAggregationFeatureRecord> records, StoreMetadataProperties storeMetadataProperties);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
