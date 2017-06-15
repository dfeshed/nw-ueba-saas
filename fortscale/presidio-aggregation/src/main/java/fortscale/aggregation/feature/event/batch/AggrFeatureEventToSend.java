package fortscale.aggregation.feature.event.batch;

import fortscale.common.feature.Feature;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;



@Document(collection = AggrFeatureEventToSend.COLLECTION_NAME)
public class AggrFeatureEventToSend {
    public static final String COLLECTION_NAME = "AggrFeatureEventToSend";

    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
    public static final String BUCKET_ID_FIELD = "bucketId";
    public static final String AGGREGATED_FEATURE_EVENT_CONF_NAME_FIELD = "aggregatedFeatureEventConfName";
    public static final String CONTEXT_FIELD = "context";
    public static final String FEATURE_FIELD = "feature";


    public AggrFeatureEventToSend(){}

    public AggrFeatureEventToSend(String bucketId, String aggregatedFeatureEventConfName, Map<String, String> context, Feature feature, Long startTimeSec, Long endTimeSec){
        this.bucketId = bucketId;
        this.aggregatedFeatureEventConfName = aggregatedFeatureEventConfName;
        this.context = context;
        this.feature = feature;
        this.startTime = startTimeSec;
        this.endTime = endTimeSec;
    }

    @Id
    private String id;


    @Field(START_TIME_FIELD)
    private Long startTime;
    @Indexed
    @Field(END_TIME_FIELD)
    private Long endTime;
    @Field(BUCKET_ID_FIELD)
    private String bucketId;
    @Field(AGGREGATED_FEATURE_EVENT_CONF_NAME_FIELD)
    private String aggregatedFeatureEventConfName;
    @Field(CONTEXT_FIELD)
    private Map<String, String> context;
    @Field(FEATURE_FIELD)
    private Feature feature;

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

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }
}
