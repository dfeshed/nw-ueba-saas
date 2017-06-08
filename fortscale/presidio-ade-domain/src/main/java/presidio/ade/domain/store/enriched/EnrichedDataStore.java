package presidio.ade.domain.store.enriched;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * ADE enriched data CRUD operations.
 *
 * Created by barak_schuster on 5/21/17.
 */
public interface EnrichedDataStore {
	/**
	 * stores the given records
	 *
	 * @param recordsMetadata describing the records (which data source, etc.)
	 * @param records         to be stored
	 */
	void store(EnrichedRecordsMetadata recordsMetadata, List<? extends EnrichedRecord> records);

	/**
	 * cleanup store by filtering params
	 *
	 * @param cleanupParams to build the remove query
	 */
	void cleanup(AdeDataStoreCleanupParams cleanupParams);
}
