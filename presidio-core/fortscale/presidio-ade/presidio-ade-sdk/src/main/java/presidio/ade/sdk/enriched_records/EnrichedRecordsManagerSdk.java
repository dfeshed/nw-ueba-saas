package presidio.ade.sdk.enriched_records;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.common.RunId;
import presidio.ade.sdk.historical_runs.HistoricalRunsManagerSdk;
import presidio.ade.sdk.online_run.OnlineRunManagerSdk;

import java.util.List;

/**
 * Provides the ADE's consumers with APIs related to Enriched Records.
 *
 * @author Lior Govrin
 */
public interface EnrichedRecordsManagerSdk {
	/**
	 * Persist the given {@link EnrichedRecord}s to the database.
	 * These {@link EnrichedRecord}s will be processed once the relevant
	 * {@link HistoricalRunsManagerSdk#processNextHistoricalTimeRange(RunId)} or
	 * {@link OnlineRunManagerSdk#processNextOnlineTimeRange()} call occurs.
	 *
	 * @param metadata The metadata of the given records
	 * @param records  the records that should be stored
	 */
	void storeEnrichedRecords(EnrichedRecordsMetadata metadata, List<? extends EnrichedRecord> records);

}
