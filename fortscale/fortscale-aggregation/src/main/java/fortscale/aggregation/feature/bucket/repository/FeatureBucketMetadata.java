package fortscale.aggregation.feature.bucket.repository;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = FeatureBucketMetadata.COLLECTION_NAME)
@CompoundIndexes({
		@CompoundIndex(name = "is_synced_end_time", def = "{'isSynced': -1, 'endTime': -1}"),
		@CompoundIndex(name = "end_time_sync_time", def = "{'endTime': -1, 'syncTime': -1}"),
})
public class FeatureBucketMetadata {
	public static final String COLLECTION_NAME = "FeatureBucketMetadata";

	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String BUCKET_ID_FIELD = "bucketId";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";

	public static final String IS_SYNCED_FIELD = "isSynced";
	public static final String SYNC_TIME_FIELD = "syncTime";

	@Id
	private String id;

	@Field(START_TIME_FIELD)
	private Long startTime;
	@Field(END_TIME_FIELD)
	private Long endTime;
	@Field(STRATEGY_ID_FIELD)
	private String strategyId;
	@Field(BUCKET_ID_FIELD)
	private String bucketId;
	@Field(FEATURE_BUCKET_CONF_NAME_FIELD)
	private String featureBucketConfName;

	@Field(IS_SYNCED_FIELD)
	private Boolean isSynced = false;
	@Field(SYNC_TIME_FIELD)
	@Indexed(sparse = true)
	private Long syncTime;

	public FeatureBucketMetadata() {}

	public FeatureBucketMetadata(FeatureBucket featureBucket) {
		this.startTime = featureBucket.getStartTime();
		this.endTime = featureBucket.getEndTime();
		this.strategyId = featureBucket.getStrategyId();
		this.bucketId = featureBucket.getBucketId();
		this.featureBucketConfName = featureBucket.getFeatureBucketConfName();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(String strategyId) {
		this.strategyId = strategyId;
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public String getId() {
		return id;
	}

	public String getFeatureBucketConfName() {
		return featureBucketConfName;
	}

	public void setFeatureBucketConfName(String featureBucketConfName) {
		this.featureBucketConfName = featureBucketConfName;
	}

	public boolean isSynced() {
		return isSynced;
	}

	public void setSynced(boolean isSynced) {
		this.isSynced = isSynced;
	}

	public Long getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Long syncTime) {
		this.syncTime = syncTime;
	}

	@Override
	public String toString() {
		return "FeatureBucketMetadata{" +
				"startTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(startTime)) +
				", endTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)) +
				", id='" + id + '\'' +
				", bucketId='" + bucketId + '\'' +
				'}';
	}
}
