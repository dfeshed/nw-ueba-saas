package presidio.ade.sdk.ttl;


import java.time.Duration;
import java.time.Instant;

/**
 * Cleanup enriched data
 */
public interface TtlServiceSdk {
	/**
	 * cleanup enriched data until the given instant according to ttl and cleanupInterval.
	 *
	 * @param until remove records until the given instant according to ttl and cleanupInterval.
	 * @param ttl records of tll logical duration are stored.
	 * @param cleanupInterval cleanup interval
	 */
	void cleanupEnrichedData(Instant until, Duration ttl, Duration cleanupInterval);
}
