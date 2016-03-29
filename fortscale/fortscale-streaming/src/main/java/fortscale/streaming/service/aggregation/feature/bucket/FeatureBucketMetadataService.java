package fortscale.streaming.service.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.streaming.service.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable(preConstruction=true)
public class FeatureBucketMetadataService {
	private static final long POLL_SLEEP_TIME = 5000;

	@Autowired
	private FeatureBucketMetadataRepository featureBucketMetadataRepository;

	private final String featureBucketConfNameToMonitor;
	private long epochEndTimeBarrier;

	public FeatureBucketMetadataService(FeatureBucketConf featureBucketConfToMonitor, long epochEndTimeBarrier) {
		featureBucketConfNameToMonitor = featureBucketConfToMonitor.getName();
		this.epochEndTimeBarrier = epochEndTimeBarrier;
	}

	private boolean hasUnsyncedBuckets() {
		return featureBucketMetadataRepository.findByisSyncedFalseAndEndTimeLessThan(epochEndTimeBarrier).stream()
				.anyMatch(metadata -> metadata.getFeatureBucketConfName().equals(featureBucketConfNameToMonitor));
	}

	public boolean waitForSync() {
		while (hasUnsyncedBuckets()) {
			try {
				Thread.sleep(POLL_SLEEP_TIME);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}
}
