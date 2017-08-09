package fortscale.aggregation.domain.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;




@Document(collection=FeatureBucketAggrMetadata.COLLECTION_NAME)
public class FeatureBucketAggrMetadata {
	public static final String COLLECTION_NAME = "FeatureBucketAggrMetadata";

	public static final String END_TIME_FIELD = "endTime";
	public static final String BUCKET_ID_FIELD = "bucketId";
	public static final String FEATURE_BUCKET_CONF_NAME_FIELD = "featureBucketConfName";
	
	@Id
	private String id;

	@Indexed
	@Field(END_TIME_FIELD)
	private Long endTime;
	@Indexed
	@Field(BUCKET_ID_FIELD)
	private String bucketId;
	@Field(FEATURE_BUCKET_CONF_NAME_FIELD)
	private String featureBucketConfName;
	

	public FeatureBucketAggrMetadata(){}
	
	public FeatureBucketAggrMetadata(FeatureBucket featureBucket) {
		this.endTime = featureBucket.getEndTime().toEpochMilli();
		this.bucketId = featureBucket.getBucketId();
		this.featureBucketConfName = featureBucket.getFeatureBucketConfName();
	}



	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
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

	
	@Override
	public String toString() {
		return "FeatureBucketAggrMetadata{" +
				", endTime=" + TimeUtils.getFormattedTime(TimestampUtils.convertToMilliSeconds(endTime)) +
				", id='" + id + '\'' +
				", bucketId='" + bucketId + '\'' +
				'}';
	}
}
