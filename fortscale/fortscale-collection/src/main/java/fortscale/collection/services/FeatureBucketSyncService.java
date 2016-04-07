package fortscale.collection.services;

import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import fortscale.utils.kafka.KafkaEventsWriter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configurable(preConstruction = true)
public class FeatureBucketSyncService {
	private static final long MILLIS_TO_SLEEP_BETWEEN_SYNC_CHECKS = 1000;

	@Autowired
	private FeatureBucketMetadataRepository featureBucketMetadataRepository;

	@Value("${fortscale.aggregation.control.topic}")
	private String controlTopic;

	private Collection<String> featureBucketConfNames;
	private long secondsBetweenSyncs;
	private long maxSyncGapInSeconds;
	private long timeoutInMillis;

	private long lastTimeInSeconds;
	private KafkaEventsWriter sender;

	/**
	 * @param featureBucketConfNames the names of the feature buckets that should be synced.
	 * @param secondsBetweenSyncs    the range in seconds between syncs (e.g. if a daily
	 *                               range is given, a sync will be made at most once a day).
	 * @param maxSyncGapInSeconds    when requesting a sync, the service blocks until the sync for
	 *                               the beginning of the current range minus this gap is complete.
	 * @param timeoutInSeconds       this service may block, so after the given timeout an exception
	 *                               will be thrown. If 0 is given, there won't be any timeout.
	 */
	public FeatureBucketSyncService(
			Collection<String> featureBucketConfNames, long secondsBetweenSyncs,
			long maxSyncGapInSeconds, long timeoutInSeconds) {

		this.featureBucketConfNames = featureBucketConfNames;
		this.secondsBetweenSyncs = secondsBetweenSyncs;
		this.maxSyncGapInSeconds = maxSyncGapInSeconds;
		this.timeoutInMillis = TimeUnit.SECONDS.toMillis(timeoutInSeconds);

		lastTimeInSeconds = -1;
		sender = new KafkaEventsWriter(controlTopic); // TODO: Cannot be autowired
	}

	/**
	 * Sync all the feature buckets until now, only if the last sync was made in a time range earlier
	 * than the current time range (secondsBetweenSyncs passed to the c'tor defines the range's length).
	 * If a sync is made, the function will block until all feature buckets are synced
	 * (up to maxSyncGapInSeconds seconds from currentTimeInSeconds).
	 *
	 * @param currentTimeInSeconds the current time in seconds.
	 * @throws TimeoutException if timeout is defined and exceeded.
	 */
	public void syncIfNeeded(long currentTimeInSeconds) throws TimeoutException {
		long currentRangeIndex = currentTimeInSeconds / secondsBetweenSyncs;
		if (lastTimeInSeconds == -1) lastTimeInSeconds = currentTimeInSeconds;
		long lastRangeIndex = lastTimeInSeconds / secondsBetweenSyncs;

		if (currentRangeIndex > lastRangeIndex) {
			sync(currentRangeIndex * secondsBetweenSyncs - maxSyncGapInSeconds);
		}

		lastTimeInSeconds = currentTimeInSeconds;
	}

	/**
	 * Sync all the feature buckets, and block until all of them are synced.
	 *
	 * @param currentTimeInSeconds the current time in seconds.
	 * @throws TimeoutException if timeout is defined and exceeded.
	 */
	public void syncForcefully(long currentTimeInSeconds) throws TimeoutException {
		sync(currentTimeInSeconds);
	}

	private boolean hasUnsyncedBuckets(long untilEpochtime) {
		return featureBucketMetadataRepository.findByIsSyncedFalseAndEndTimeLessThan(untilEpochtime).stream()
				.anyMatch(metadata -> featureBucketConfNames.contains(metadata.getFeatureBucketConfName()));
	}

	@SuppressWarnings("EmptyCatchBlock")
	private void waitForSync(long untilEpochtime) throws TimeoutException {
		long startTimeMillis = System.currentTimeMillis();

		while (hasUnsyncedBuckets(untilEpochtime)) {
			if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeMillis > timeoutInMillis) {
				String msg1 = String.format("Did not sync metadata of all feature buckets in %d seconds.",
						TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
				String msg2 = String.format("Requested epochtime = %d.", untilEpochtime);
				throw new TimeoutException(String.format("%s %s", msg1, msg2));
			}

			try {
				Thread.sleep(MILLIS_TO_SLEEP_BETWEEN_SYNC_CHECKS);
			} catch (InterruptedException e) {
			}
		}
	}

	private void sync(long untilEpochtime) throws TimeoutException {
		sender.send(null, new JSONObject().toJSONString(JSONStyle.NO_COMPRESS));
		waitForSync(untilEpochtime);
	}
}
