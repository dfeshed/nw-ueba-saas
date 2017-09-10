package presidio.ade.sdk.ttl;


import java.time.Duration;
import java.time.Instant;

/**
 *
 */
public interface TtlServiceSdk {
	/**
	 * cleanup enriched collections until the given instant according to ttl and cleanupInterval.
	 *
	 * @param instant remove records until the given instant according to ttl and cleanupInterval.
	 * @param ttl records of tll logical duration are stored.
	 * @param cleanupInterval cleanup interval
	 */
	void cleanupEnrichedCollections(Instant instant, Duration ttl, Duration cleanupInterval);
}
