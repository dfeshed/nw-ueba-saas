package presidio.ade.sdk.enriched_records;

import fortscale.domain.core.EnrichedRecordsMetadata;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
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

	/**
	 * Get a list of all the scored enriched records with the given feature name, context ID and time range.
	 *
	 * @param featureName the name of the scored feature
	 * @param contextId   the context ID (i.e. username)
	 * @param timeRange   the start and end instants of the records
	 * @return a list of {@link AdeScoredEnrichedRecord}s
	 */
	List<AdeScoredEnrichedRecord> getScoredEnrichedRecords(String featureName, String contextId, TimeRange timeRange);
}
