package fortscale.aggregation.domain.feature.event;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;




@Document(collection=FeatureBucketAggrSendingQueue.COLLECTION_NAME)
public class FeatureBucketAggrSendingQueue {
	public static final String COLLECTION_NAME = "FeatureBucketAggrSendingQueue";

	public static final String FIRE_TIME_FIELD = "fireTime";
	public static final String BUCKET_ID_FIELD = "bucketId";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";
	public static final String END_TIME_FIELD = "endTime";
	
	@Id
	private String id;

	@Indexed
	@Field(FIRE_TIME_FIELD)
	private Long fireTime;
	@Indexed
	@Field(BUCKET_ID_FIELD)
	private String bucketId;
	@Field(FEATURE_BUCKET_CONF_NAME_FIELD)
	private String featureBucketConfName;
	@Field(END_TIME_FIELD)
	private Long endTime;

	public FeatureBucketAggrSendingQueue(){}
	
	public FeatureBucketAggrSendingQueue(String featureBucketConfName, String bucketId, Long fireTime, Long endTime) {
		this.fireTime = fireTime;
		this.bucketId = bucketId;
		this.featureBucketConfName = featureBucketConfName;
		this.endTime = endTime;
	}



	public long getFireTime() {
		return fireTime;
	}

	public void setFireTime(long fireTime) {
		this.fireTime = fireTime;
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
	
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	@Override
	public String toString() {
		return "FeatureBucketAggrSendingQueue{" +
				", endTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)) +
				", fireTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(fireTime)) +
				", id='" + id + '\'' +
				", bucketId='" + bucketId + '\'' +
				'}';
	}
}

