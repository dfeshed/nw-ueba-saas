package fortscale.collection.services;

import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable(preConstruction = true)
public class FeatureBucketSyncService {
	private static final long POLL_SLEEP_TIME = 5000;

	@Autowired
	private FeatureBucketMetadataRepository featureBucketMetadataRepository;

	@Value("${fortscale.aggregation.control.topic}")
	private String controlTopic;

	private String featureBucketConfNameToSync;
	private long intervalLengthInSeconds;
	private long maxSyncGapInSeconds;
	private long lastCurrentTimeInSeconds;
	private KafkaEventsWriter sender;

	/**
	 * @param featureBucketConfNameToSync the name of the bucket which should be synced.
	 * @param intervalLengthInSeconds     the length of the interval used for throttling (e.g. - if given
	 *                                    a daily interval, a sync will be made at most once a day).
	 * @param maxSyncGapInSeconds         when a sync is made, the service blocks until the sync is complete
	 *                                    for the current interval's beginning minus this gap.
	 */
	public FeatureBucketSyncService(String featureBucketConfNameToSync,
									long intervalLengthInSeconds,
									long maxSyncGapInSeconds) {
		this.featureBucketConfNameToSync = featureBucketConfNameToSync;
		this.intervalLengthInSeconds = intervalLengthInSeconds;
		this.maxSyncGapInSeconds = maxSyncGapInSeconds;
		lastCurrentTimeInSeconds = 0;
		sender = new KafkaEventsWriter(controlTopic);
	}

	/**
	 * Sync all the buckets until now, but only if the last sync was made in a time interval earlier than the
	 * current time interval (intervalLengthInSeconds passed to the c'tor defines the interval's length).
	 * If a sync is made, the function will block until all buckets are synced (up to maxSyncGapInSeconds
	 * seconds from currentTimeInSeconds).
	 *
	 * @param currentTimeInSeconds the current time.
	 * @param timeout              this function may block (if a sync is indeed needed). After the given timeout an
	 *                             Exception will be thrown. If given a negative number there won't be any timeout.
	 * @throws Exception if timeout is defined and exceeded.
	 */
	public void syncIfNeeded(long currentTimeInSeconds, long timeout) throws Exception {
		long currentIntervalIndex = currentTimeInSeconds / intervalLengthInSeconds;
		long lastIntervalIndex = lastCurrentTimeInSeconds / intervalLengthInSeconds;
		if (currentIntervalIndex > lastIntervalIndex) {
			sync(currentIntervalIndex * intervalLengthInSeconds - maxSyncGapInSeconds, timeout);
		}
		lastCurrentTimeInSeconds = currentTimeInSeconds;
	}

	/**
	 * Sync all the buckets, and block until all buckets until currentTimeInSeconds are synced.
	 *
	 * @param currentTimeInSeconds the current time.
	 * @param timeout              this function will block until the sync is complete. After the given timeout an
	 *                             Exception will be thrown. If given a negative number there won't be any timeout.
	 * @throws Exception if timeout is defined and exceeded.
	 */
	public void syncForcefully(long currentTimeInSeconds, long timeout) throws Exception {
		sync(currentTimeInSeconds, timeout);
	}

	private boolean hasUnsyncedBuckets(long untilEpochtime) {
		return featureBucketMetadataRepository.findByIsSyncedFalseAndEndTimeLessThan(untilEpochtime).stream()
				.anyMatch(metadata -> metadata.getFeatureBucketConfName().equals(featureBucketConfNameToSync));
	}

	private void waitForSync(long untilEpochtime, long timeout) throws Exception {
		long currentTime = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		while (hasUnsyncedBuckets(untilEpochtime)) {
			if (timeout > 0 && TimestampUtils.convertToSeconds(System.currentTimeMillis()) - currentTime > timeout) {
				throw new Exception("Failed after " + timeout + " seconds");
			}
			Thread.sleep(POLL_SLEEP_TIME);
		}
	}

	private void sync(long untilEpochtime, long timeout) throws Exception {
		sender.send(null, null);
		waitForSync(untilEpochtime, timeout);
	}
}
