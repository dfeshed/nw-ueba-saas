package fortscale.aggregation.feature.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.domain.core.FeatureScore;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class AggrEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String EVENT_FILED_DATA_SOURCE = "data_source";
    public static final String EVENT_FIELD_FEATURE_TYPE = "aggregated_feature_type";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_NAME = "aggregated_feature_name";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_VALUE = "aggregated_feature_value";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_INFO = "aggregated_feature_info";
    public static final String EVENT_FIELD_BUCKET_CONF_NAME = "bucket_conf_name";
    public static final String EVENT_FIELD_CONTEXT = "context";
    public static final String EVENT_FIELD_CONTEXT_ID = "contextId";
    public static final String EVENT_FIELD_CREATION_EPOCHTIME = "creation_epochtime";
    public static final String EVENT_FIELD_CREATION_DATE_TIME = "creation_date_time";
    public static final String EVENT_FIELD_START_TIME_UNIX = "start_time_unix";
    public static final String EVENT_FIELD_START_TIME = "start_time";
    public static final String EVENT_FIELD_END_TIME_UNIX = "end_time_unix";
    public static final String EVENT_FIELD_END_TIME = "end_time";
    public static final String EVENT_FIELD_DATA_SOURCES = "data_sources";
    public static final String EVENT_FIELD_SCORE = "score";
    public static final String EVENT_FIELD_FEATURE_SCORES = "feature_scores";

    public static final String AGGREGATED_FEATURE_TYPE_F_VALUE = "F";
    public static final String AGGREGATED_FEATURE_TYPE_P_VALUE = "P";

    // Event Fields
    @SuppressWarnings("unused")
    @Id
    private String id;

    @Field(EVENT_FILED_DATA_SOURCE)
    private String dataSource;

    @Field(EVENT_FIELD_FEATURE_TYPE)
    String featureType;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_NAME)
    String aggregatedFeatureName;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_VALUE)
    Double aggregatedFeatureValue;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_INFO)
    Map<String, Object> aggregatedFeatureInfo;

    @Field(EVENT_FIELD_BUCKET_CONF_NAME)
    String bucketConfName;

    @Field(EVENT_FIELD_CONTEXT)
    Map<String, String> context;

    @Field(EVENT_FIELD_CONTEXT_ID)
    String contextId;

    @Field(EVENT_FIELD_CREATION_EPOCHTIME)
    Long creationEpochTime;

    @Field(EVENT_FIELD_CREATION_DATE_TIME)
    Date creationDateTime;

    @Field(EVENT_FIELD_START_TIME)
    Date startTime;

    @Field(EVENT_FIELD_START_TIME_UNIX)
    Long startTimeUnix;

    @Field(EVENT_FIELD_END_TIME)
    Date endTime;

    @Field(EVENT_FIELD_END_TIME_UNIX)
    Long endTimeUnix;

    @Field(EVENT_FIELD_DATA_SOURCES)
    List<String> dataSources;

    @Field(EVENT_FIELD_SCORE)
    Double score;

    @Field(EVENT_FIELD_FEATURE_SCORES)
    List<FeatureScore> featureScores;

    public AggrEvent() {}

    public AggrEvent(
            String dataSource, String featureType, String aggregatedFeatureName,
            Double aggregatedFeatureValue, Map<String, Object> aggregatedFeatureInfo,
            String bucketConfName, Map<String, String> context, String contextId,
            Long creationEpochTimeSeconds, Long startTimeUnixSeconds, Long endTimeUnixSeconds,
            List<String> dataSources, Double score, List<FeatureScore> featureScores) {

        this.dataSource = dataSource;
        this.featureType = featureType;
        this.aggregatedFeatureName = aggregatedFeatureName;
        this.aggregatedFeatureValue = aggregatedFeatureValue;
        this.aggregatedFeatureInfo = aggregatedFeatureInfo;

        this.bucketConfName = bucketConfName;
        this.context = context;
        this.contextId = contextId;
        this.creationEpochTime = creationEpochTimeSeconds;
        creationDateTime = new Date(TimestampUtils.convertToMilliSeconds(creationEpochTimeSeconds));
        this.startTimeUnix  = startTimeUnixSeconds;
        startTime = new Date(TimestampUtils.convertToMilliSeconds(startTimeUnixSeconds));
        this.endTimeUnix  = endTimeUnixSeconds;
        endTime  = new Date(TimestampUtils.convertToMilliSeconds(endTimeUnixSeconds));

        this.dataSources = dataSources;
        this.score = score;
        this.featureScores = featureScores;
    }

    public String getFeatureType() {
        return featureType;
    }

    public String getDataSource() {
        return dataSource;
    }

    public boolean isOfTypeF() {
        return AGGREGATED_FEATURE_TYPE_F_VALUE.equals(getFeatureType());
    }

    public boolean isOfTypeP() {
        return AGGREGATED_FEATURE_TYPE_P_VALUE.equals(getFeatureType());
    }

    public String getBucketConfName() {
        return bucketConfName;
    }

    public String getAggregatedFeatureName() {
        return aggregatedFeatureName;
    }

    public Double getAggregatedFeatureValue() {
        return aggregatedFeatureValue;
    }

    public Map<String, Object> getAggregatedFeatureInfo() {
        return aggregatedFeatureInfo;
    }

    public Long getCreationEpochTime() {
        return creationEpochTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Long getStartTimeUnix() {
        return startTimeUnix;
    }

    public Double getScore() {
        return score;
    }

    public List<FeatureScore> getFeatureScores() {
        return featureScores;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public String getContextId() {
        return contextId;
    }

    public Map<String, String> getContext(List<String> contextFields) {
        if (contextFields == null) {
            return null;
        }

        Map<String, String> context = new HashMap<>();
        for (String contextField : contextFields) {
            if (this.context.containsKey(contextField)) {
                context.put(contextField, this.context.get(contextField));
            } else {
                // The requested context cannot be fully deduced from the event,
                // because one of the context fields is missing
                return Collections.emptyMap();
            }
        }

        return context;
    }

    public Long getEndTimeUnix() {
        return endTimeUnix;
    }

    public List<String> getDataSources() {
        return dataSources;
    }

    /**
     * ToString, you know ...
     *
     * @return object as a String
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
