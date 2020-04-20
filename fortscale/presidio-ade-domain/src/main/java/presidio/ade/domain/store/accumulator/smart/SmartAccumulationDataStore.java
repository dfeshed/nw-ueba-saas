package presidio.ade.domain.store.accumulator.smart;

import fortscale.utils.store.record.StoreMetadataProperties;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.util.List;


public interface SmartAccumulationDataStore extends SmartAccumulationDataReader {
    /**
     * stores the given records
     *
     * @param records to be stored
     */
    void store(List<? extends AdeContextualAggregatedRecord> records, String configurationName, StoreMetadataProperties storeMetadataProperties);

}
