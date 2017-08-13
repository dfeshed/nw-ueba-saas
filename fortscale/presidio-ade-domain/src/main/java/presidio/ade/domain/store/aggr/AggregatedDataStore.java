package presidio.ade.domain.store.aggr;

import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * Created by barak_schuster on 7/10/17.
 */
public interface AggregatedDataStore extends AggregatedDataReader {
    /**
     * stores the given records
     *
     * @param records         to be stored
     * @param aggregatedFeatureType
     */
    void store(List<? extends AdeContextualAggregatedRecord> records, AggregatedFeatureType aggregatedFeatureType);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
