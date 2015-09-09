package fortscale.aggregation.feature.event;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by amira on 23/08/2015.
 */
public class AggrEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String EVENT_FILED_DATA_SOURCE = "data_source";
    public static final String EVENT_FIELD_FEATURE_TYPE = "aggregated_feature_type";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_NAME = "aggregated_feature_name";
    public static final String EVENT_FIELD_AGGREFGATED_FEATURE_VALUE = "aggregated_feature_value";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_INFO = "aggregated_feature_info";
    public static final String EVENT_FIELD_BUCKET_CONF_NAME = "bucket_conf_name";
    public static final String EVENT_FIELD_CONTEXT = "context";
    public static final String EVENT_FIELD_CREATION_EPOCHTIME = "creation_epochtime";
    public static final String EVENT_FIELD_CREATION_DATE_TIME = "creation_date_time";
    public static final String EVENT_FIELD_START_TIME_UNIX = "start_time_unix";
    public static final String EVENT_FIELD_START_TIME = "start_time";
    public static final String EVENT_FIELD_END_TIME_UNIX = "end_time_unix";
    public static final String EVENT_FIELD_END_TIME = "end_time";
    public static final String EVENT_FIELD_DATA_SOURCES = "data_sources";
    public static final String EVENT_FIELD_SCORE = "score";

    private static final String AGGREGATED_FEATURE_TYPE_F_VALUE = "F";
    private static final String AGGREGATED_FEATURE_TYPE_P_VALUE = "P";


    // Event Fields
    @Id
    private String id;
    
    @Field(EVENT_FILED_DATA_SOURCE)
    private String dataSource;

    @Field(EVENT_FIELD_FEATURE_TYPE)
    String featureType;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_NAME)
    String aggregatedFeatureName;

    @Field(EVENT_FIELD_AGGREFGATED_FEATURE_VALUE)
    Double aggregatedFeatureValue;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_INFO)
    Map<String, Object> aggregatedFeatureInfo;

    @Indexed
    @Field(EVENT_FIELD_BUCKET_CONF_NAME)
    String bucketConfName;

    @Indexed
    @Field(EVENT_FIELD_CONTEXT)
    Map<String, String> context;

    @Field(EVENT_FIELD_CREATION_EPOCHTIME)
    Long creationEpochTime;

    @Field(EVENT_FIELD_CREATION_DATE_TIME)
    Date creationDateTime;

    @Field(EVENT_FIELD_START_TIME)
    Date startTime;

    @Indexed
    @Field(EVENT_FIELD_START_TIME_UNIX)
    Long startTimeUnix;

  //The ttl for each document is 1 year and 3 months
    @Indexed(unique = false, expireAfterSeconds=60*60*24*30*15)
    @Field(EVENT_FIELD_END_TIME)
    Date endTime;

    @Field(EVENT_FIELD_END_TIME_UNIX)
    Long endTimeUnix;

    @Field(EVENT_FIELD_DATA_SOURCES)
    List<String> dataSources;

    @Field(EVENT_FIELD_SCORE)
    Double score;

    

    public AggrEvent() {}

    public AggrEvent(String dataSource, String featureType, String aggregatedFeatureName, Double aggregatedFeatureValue, Map<String, Object> aggregatedFeatureInfo, String bucketConfName, Map<String, String> context, 
    		Long creationEpochTime, Long startTimeUnix, Long endTimeUnix, List<String> dataSources, Double score) {
    	this.dataSource = dataSource;
        this.featureType = featureType;
        this.aggregatedFeatureName = aggregatedFeatureName;
        this.aggregatedFeatureValue = aggregatedFeatureValue;
        this.aggregatedFeatureInfo = aggregatedFeatureInfo;
        
        this.bucketConfName = bucketConfName;
        this.context = context;
        this.creationEpochTime = creationEpochTime;
        creationDateTime = new Date(creationEpochTime);
        this.startTimeUnix  = startTimeUnix;
        startTime = new Date(startTimeUnix);
        this.endTimeUnix  = endTimeUnix;
        endTime  = new Date(endTimeUnix);
        
        this.dataSources = dataSources;
        this.score = score;
    }

//    public JSONObject getAsJSONObject() {
//        JSONObject event =  buildEvent(dataSource, featureType, aggregatedFeatureName, aggregatedFeatureValue, aggregatedFeatureInfo,
//                bucketConfName, context, startTimeUnix, endTimeUnix.getTime(), dataSources, creationEpochTime);
//        if(score!=null) {
//            event.put(EVENT_FIELD_SCORE, score);
//        }
//        return event;
//    }

    
    
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

	public Long getStartTimeUnix() {
		return startTimeUnix;
	}

	public Double getScore() {
        return score;
    }

    public Map<String, String> getContext() {
        return context;
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

    public Long getStartTime() {
        return startTimeUnix;
    }

    public Long getEndTimeUnix() {
        return endTimeUnix;
    }

    public List<String> getDataSources() {return dataSources; }

}
