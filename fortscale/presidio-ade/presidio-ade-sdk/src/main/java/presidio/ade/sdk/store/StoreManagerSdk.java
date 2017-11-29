package presidio.ade.sdk.store;


import fortscale.utils.time.TimeRange;

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
	 * cleanup enriched data in mentioned timeRange
	 * @param timeRange timeRange, when enriched data should be cleaned.
	 */
	void cleanupEnrichedData(TimeRange timeRange);
}
