package fortscale.aggregation.feature.event.batch;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = AggrFeatureEventToSend.COLLECTION_NAME)
public class AggrFeatureEventToSend {
    public static final String COLLECTION_NAME = "AggrFeatureEventToSend";

    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
    public static final String BUCKET_ID_FIELD = "bucketId";
    public static final String AGGREGATED_FEATURE_EVENT_CONF_NAME_FIELD = "aggregatedFeatureEventConfName";



    @Id
    private String id;

    @Field(START_TIME_FIELD)
    private Long startTime;
    @Field(END_TIME_FIELD)
    private Long endTime;
    @Field(BUCKET_ID_FIELD)
    private String bucketId;
    @Field(AGGREGATED_FEATURE_EVENT_CONF_NAME_FIELD)
    private String aggregatedFeatureEventConfName;

    public String getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getAggregatedFeatureEventConfName() {
        return aggregatedFeatureEventConfName;
    }

    public void setAggregatedFeatureEventConfName(String aggregatedFeatureEventConfName) {
        this.aggregatedFeatureEventConfName = aggregatedFeatureEventConfName;
    }
}
