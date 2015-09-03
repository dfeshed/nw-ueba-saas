package fortscale.aggregation.feature.event;

import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by amira on 23/08/2015.
 */
public class AggrEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String EVENT_FIELD_EVENT_TYPE ="event_type";
    public static final String EVENT_FIELD_EVENT_TYPE_VALUE ="aggr_event";
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
    public static final String EVENT_FIELD_EPOCHTIME = "date_time_unix";
    public static final String EVENT_FIELD_DATA_SOURCES = "data_sources";
    public static final String EVENT_FIELD_SCORE = "score";

    private static final String AGGREGATED_FEATURE_TYPE_F_VALUE = "F";
    private static final String AGGREGATED_FEATURE_TYPE_P_VALUE = "P";

    private static final SimpleDateFormat format = getSimpleDateFormat();


    // Event Fields
    @Id
    private String id;

    @Field(EVENT_FIELD_EVENT_TYPE)
    String eventType;

    @Field(EVENT_FILED_DATA_SOURCE)
    String dataSource;

    @Field(EVENT_FIELD_FEATURE_TYPE)
    String featureType;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_NAME)
    String aggregatedFeatureName;

    @Field(EVENT_FIELD_AGGREFGATED_FEATURE_VALUE)
    Double aggregatedFeatureValue;

    @Field(EVENT_FIELD_AGGREGATED_FEATURE_INFO)
    JSONObject aggregatedFeatureInfo;

    @Indexed
    @Field(EVENT_FIELD_BUCKET_CONF_NAME)
    String bucketConfName;

    @Indexed
    @Field(EVENT_FIELD_CONTEXT)
    Map<String, String> context;

    @Field(EVENT_FIELD_CREATION_EPOCHTIME)
    Long creationEpochTime;

    @Field(EVENT_FIELD_CREATION_DATE_TIME)
    String creationDateTime;

    @Field(EVENT_FIELD_START_TIME)
    String startTime;

    @Indexed
    @Field(EVENT_FIELD_START_TIME_UNIX)
    Long startTimeUnix;

    @Field(EVENT_FIELD_END_TIME)
    String endTime;

    //The ttl for each document is 1 year and 3 months
    @Indexed(unique = false, expireAfterSeconds=60*60*24*30*15)
    @Field(EVENT_FIELD_END_TIME_UNIX)
    Date endTimeUnix;

    @Field(EVENT_FIELD_EPOCHTIME)
    Long epochTime;

    @Field(EVENT_FIELD_DATA_SOURCES)
    JSONArray dataSources;

    @Field(EVENT_FIELD_SCORE)
    Double score;

    private static SimpleDateFormat getSimpleDateFormat(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format;
    }

    public AggrEvent() {}

    public AggrEvent(JSONObject event) {
        Assert.notNull(event);
        eventType = event.getAsString(EVENT_FIELD_EVENT_TYPE);
        dataSource = event.getAsString(EVENT_FILED_DATA_SOURCE);
        featureType = event.getAsString(EVENT_FIELD_FEATURE_TYPE);
        aggregatedFeatureName = event.getAsString(EVENT_FIELD_AGGREGATED_FEATURE_NAME);
        aggregatedFeatureValue = ConversionUtils.convertToDouble(event.get(EVENT_FIELD_AGGREFGATED_FEATURE_VALUE));
        if (event.get(EVENT_FIELD_AGGREGATED_FEATURE_INFO) != null) {
            aggregatedFeatureInfo = new JSONObject((Map) event.get(EVENT_FIELD_AGGREGATED_FEATURE_INFO));
        }
        bucketConfName = event.getAsString(EVENT_FIELD_BUCKET_CONF_NAME);
        context = (Map)event.get(EVENT_FIELD_CONTEXT);
        creationEpochTime = event.getAsNumber(EVENT_FIELD_CREATION_EPOCHTIME).longValue();
        creationDateTime = event.getAsString(EVENT_FIELD_CREATION_DATE_TIME);
        startTime = event.getAsString(EVENT_FIELD_START_TIME);
        startTimeUnix  = event.getAsNumber(EVENT_FIELD_START_TIME_UNIX).longValue();
        endTime  = event.getAsString(EVENT_FIELD_END_TIME);
        endTimeUnix  = new Date(event.getAsNumber(EVENT_FIELD_END_TIME_UNIX).longValue());
        epochTime  = event.getAsNumber(EVENT_FIELD_EPOCHTIME).longValue();
        if (event.get(EVENT_FIELD_DATA_SOURCES) != null) {
            dataSources = new JSONArray();
            dataSources.addAll((ArrayList) event.get(EVENT_FIELD_DATA_SOURCES));
        }
        score = ConversionUtils.convertToDouble(event.get(EVENT_FIELD_SCORE));
    }

    public JSONObject getAsJSONObject() {
        JSONObject event =  buildEvent(dataSource, featureType, aggregatedFeatureName, aggregatedFeatureValue, aggregatedFeatureInfo,
                bucketConfName, context, startTimeUnix, endTimeUnix.getTime(), dataSources, creationEpochTime);
        if(score!=null) {
            event.put(EVENT_FIELD_SCORE, score);
        }
        return event;
    }

    public String getAggregatedFeatureType() {
        return featureType;
    }

    public boolean isOfTypeF() {
        return AGGREGATED_FEATURE_TYPE_F_VALUE.equals(getAggregatedFeatureType());
    }

    public boolean isOfTypeP() {
        return AGGREGATED_FEATURE_TYPE_P_VALUE.equals(getAggregatedFeatureType());
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

    public Long getEndTime() {
        return endTimeUnix.getTime();
    }

    public String getDataSource() {return  dataSource; }

    public JSONArray getDataSources() {return dataSources; }

    public List<String> getDataSourcesAsList() {
        ArrayList<String> list = new ArrayList<String>();
        if (dataSources != null) {
            int len = dataSources.size();
            for (int i=0;i<len;i++){
                list.add(dataSources.get(i).toString());
            }
        }

        return list;
    }

    public static JSONObject buildEvent(String dataSource, String featureType, String aggregatedFeatureName, Object value, Map<String, Object> additionalInfoMap, String bucketConfName,
                                        Map<String, String> context, Long startTimeSec, Long endTimeSec, JSONArray dataSourcesJsonArray) {
        return buildEvent(dataSource, featureType, aggregatedFeatureName, value, additionalInfoMap, bucketConfName, context, startTimeSec, endTimeSec, dataSourcesJsonArray, 0L);
    }

    public static JSONObject buildEvent(String dataSource, String featureType, String aggregatedFeatureName, Object value, Map<String, Object> additionalInfoMap, String bucketConfName,
                                 Map<String, String> context, Long startTimeSec, Long endTimeSec, JSONArray dataSourcesJsonArray, Long creationEpochTime) {
        JSONObject event = new JSONObject();
        //static data for all aggregated feature events
        event.put(EVENT_FIELD_EVENT_TYPE, AggrEvent.EVENT_FIELD_EVENT_TYPE_VALUE);

        // Feature Data
        event.put(EVENT_FILED_DATA_SOURCE, dataSource);
        event.put(EVENT_FIELD_FEATURE_TYPE, featureType);
        event.put(EVENT_FIELD_AGGREGATED_FEATURE_NAME, aggregatedFeatureName);
        event.put(EVENT_FIELD_AGGREFGATED_FEATURE_VALUE, value);
        if(additionalInfoMap!=null) {
            event.put(EVENT_FIELD_AGGREGATED_FEATURE_INFO, new JSONObject(additionalInfoMap));
        }
        event.put(EVENT_FIELD_BUCKET_CONF_NAME, bucketConfName);

        // Context
        event.put(EVENT_FIELD_CONTEXT, context);

        // Event time
        Long creation_epochtime = creationEpochTime;
        if(creationEpochTime==0) {
            creation_epochtime = System.currentTimeMillis() / 1000;
        }
        event.put(EVENT_FIELD_CREATION_EPOCHTIME, creation_epochtime);

        String date_time = format.format(new Date(creation_epochtime * 1000));
        event.put(EVENT_FIELD_CREATION_DATE_TIME, date_time);

        // Start Time
        event.put(EVENT_FIELD_START_TIME_UNIX, startTimeSec);
        String start_time = format.format(new Date(startTimeSec * 1000));
        event.put(EVENT_FIELD_START_TIME, start_time);

        // End Time
        event.put(EVENT_FIELD_END_TIME_UNIX, endTimeSec);
        String end_time = format.format(new Date(endTimeSec * 1000));
        event.put(EVENT_FIELD_END_TIME, end_time);

        // time of the event to be compared against other events from different types (raw events, entity event...)
        event.put(EVENT_FIELD_EPOCHTIME, endTimeSec);

        // Data Sources
        event.put(EVENT_FIELD_DATA_SOURCES, dataSourcesJsonArray);

        return event;
    }

}
