package presidio.ade.sdk.store;


import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Duration;
import java.time.Instant;

/**
 * Cleanup data
 */
public interface StoreManagerSdk {
	/**
	 * cleanup enriched data until the given instant according to ttl and cleanupInterval.
	 *
	 * @param until remove records until the given instant according to ttl and cleanupInterval.
	 * @param enrichedTtl records of tll logical duration are stored.
	 * @param enrichedCleanupInterval cleanup interval
	 */
	void cleanupEnrichedData(Instant until, Duration enrichedTtl, Duration enrichedCleanupInterval);

	/**
	 * Cleanup {@link EnrichedRecord}s that match the given parameters from the database.
	 *
	 * @param adeDataStoreCleanupParams the cleanup parameters
	 */
	void cleanupEnrichedRecords(AdeDataStoreCleanupParams adeDataStoreCleanupParams);
}
