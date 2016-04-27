package fortscale.aggregation.feature.event;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.domain.core.FeatureScore;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AggrFeatureEventBuilderService {

    private static final SimpleDateFormat format = getSimpleDateFormat();
    private static final String CONTEXT_ID_SEPARATOR = "#";
    private static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
    @Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
    @Value("${streaming.aggr_event.field.context}")
	private String contextFieldName;
    
    

    @Autowired
    private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;



    

    /**
     * Builds an event in the following format:
     * <pre>
     *    {
     *      "aggregated_feature_type": "F",
     *      "aggregated_feature_name": "number_of_distinct_src_machines",
     *      "aggregated_feature_value": 42,
     *      "aggregated_feature_info": {
     *          "list_of_distinct_src_machines": [
     *              "src_machine_1",
     *              "src_machine_2",
     *              "src_machine_3"
     *          ]
     *      },
     *
     *     "bucket_conf_name": "bucket_conf_1",
     *
     *     "date_time_unix": 1430460833,
     *     "date_time": "2015-05-01 06:13:53",
     *
     *     "start_time_unix": 1430460833,
     *     "start_time": "2015-05-01 06:13:53",
     *
     *     "end_time_unix": 1430460833,
     *     "end_time": "2015-05-01 06:13:53",
     *
     *     "context": {
     *          "user": "John Smith",
     *          "machine": "machine_1"
     *     },
     *
     *     "data_sources": ["ssh", "vpn"],
     *
     *     "score": 85
     *
     *   }
     *</pre>
     * @return the event as JSONObject
     */
    public JSONObject buildEvent(AggregatedFeatureEventConf conf, Map<String, String> context, Feature feature, Long startTimeSec, Long endTimeSec) throws IllegalArgumentException{
        AggrFeatureValue featureValue;
        Double value;
        Map<String, Object> additionalInfoMap;

        try {
            featureValue = (AggrFeatureValue)feature.getValue();
            value = ConversionUtils.convertToDouble(featureValue.getValue());
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Feature is null or value is null or value is not a AggrFeatureValue object: %s", feature), ex);
        }
        if(value==null) {
            throw new IllegalArgumentException(String.format("Feature value doesn't contain a 'value' element: %s", featureValue));
        }
        additionalInfoMap = featureValue.getAdditionalInformationMap();

        // Data Sources
//        JSONArray dataSourcesJsonArray = new JSONArray();
//        dataSourcesJsonArray.addAll(conf.getBucketConf().getDataSources());

        return buildEvent(aggregatedFeatureEventsConfUtilService.buildOutputBucketDataSource(conf),
                conf.getType(),
                conf.getName(),
                value,
                additionalInfoMap,
                conf.getBucketConfName(),
                context,
                startTimeSec,
                endTimeSec,
                conf.getBucketConf().getDataSources()
        );
    }
    
    private JSONObject buildEvent(String dataSource, String featureType, String aggregatedFeatureName, Double value, Map<String, Object> additionalInfoMap, String bucketConfName,
            Map<String, String> context, Long startTimeSec, Long endTimeSec, List<String> dataSources) {
    	return buildEvent(dataSource, featureType, aggregatedFeatureName, value, additionalInfoMap, bucketConfName, context, startTimeSec, endTimeSec, dataSources, 0L);
    }
    
    
    
    public JSONObject getAggrFeatureEventAsJsonObject(AggrEvent aggrEvent){
    	JSONObject jsonObject = buildEvent(aggrEvent.getDataSource(), aggrEvent.getFeatureType(), aggrEvent.getAggregatedFeatureName(), aggrEvent.getAggregatedFeatureValue(), aggrEvent.getAggregatedFeatureInfo(), 
    			aggrEvent.getBucketConfName(), aggrEvent.getContext(), aggrEvent.getStartTimeUnix(), aggrEvent.getEndTimeUnix(), aggrEvent.getDataSources(), aggrEvent.getCreationEpochTime());
    	
    	jsonObject.put(AggrEvent.EVENT_FIELD_SCORE, aggrEvent.getScore());
    	
    	return jsonObject;
    }
    
    public JSONObject buildEvent(String dataSource, String featureType, String aggregatedFeatureName, Double value, Map<String, Object> additionalInfoMap, String bucketConfName,
            Map<String, String> context, Long startTimeSec, Long endTimeSec, List<String> dataSources, Long creationEpochTime) {
		JSONObject event = new JSONObject();
		//static data for all aggregated feature events
        event.put(eventTypeFieldName, eventTypeFieldValue);
		
		// Feature Data
        setAggregatedFeatureDataSource(event, dataSource);
		event.put(dataSourceFieldName, dataSource);
		setAggregatedFeatureType(event, featureType);
		setAggregatedFeatureName(event, aggregatedFeatureName);
		setAggregatedFeatureValue(event, value);
		setAggregatedFeatureAdditionalInfo(event, additionalInfoMap);
		setAggregatedFeatureBucketConfName(event, bucketConfName);
		
		// Context
		setAggregatedFeatureContext(event, context);
		
		// Event time
		Long creation_epochtime = creationEpochTime;
		if(creation_epochtime==0) {
		    creation_epochtime = System.currentTimeMillis() / 1000;
		}
		setAggregatedFeatureCreationEpochTime(event, creation_epochtime);
		
		String date_time = format.format(new Date(creation_epochtime * 1000));
		event.put(AggrEvent.EVENT_FIELD_CREATION_DATE_TIME, date_time);
		
		// Start Time
		setAggregatedFeatureStartTimeUnix(event, startTimeSec);
		String start_time = format.format(new Date(startTimeSec * 1000));
		event.put(AggrEvent.EVENT_FIELD_START_TIME, start_time);
		
		// End Time
		setAggregatedFeatureEndTimeUnix(event, endTimeSec);
		String end_time = format.format(new Date(endTimeSec * 1000));
		event.put(AggrEvent.EVENT_FIELD_END_TIME, end_time);
		
		// time of the event to be compared against other events from different types (raw events, entity event...)
		event.put(epochtimeFieldName, endTimeSec);
		
		// Data Sources
		setAggregatedFeatureDataSources(event, dataSources);
		
		return event;
	}

    public AggrEvent buildEvent(JSONObject event) {
        String dataSource = getAggregatedFeatureDataSource(event);
        String featureType = getAggregatedFeatureType(event);
        String aggregatedFeatureName = getAggregatedFeatureName(event);
        Double aggregatedFeatureValue = getAggregatedFeatureValue(event);
        Map<String, Object> aggregatedFeatureInfo = getAggregatedFeatureAdditionalInfo(event);
        String bucketConfName = getAggregatedFeatureBucketConfName(event);
        Map<String, String> context = getAggregatedFeatureContext(event);
        String contextId = getAggregatedFeatureContextId(event);
        Long creationEpochTime = getAggregatedFeatureCreationEpochTime(event);
        Long startTimeUnix = getAggregatedFeatureStartTimeUnix(event);
        Long endTimeUnix = getAggregatedFeatureEndTimeUnix(event);
        List<String> dataSources = getAggregatedFeatureDataSources(event);
        Double score = getAggregatedFeatureScore(event);
		List<FeatureScore> featureScores = getFeatureScores(event);

        return new AggrEvent(
                dataSource, featureType, aggregatedFeatureName,
                aggregatedFeatureValue, aggregatedFeatureInfo,
                bucketConfName, context, contextId,
                creationEpochTime, startTimeUnix, endTimeUnix,
                dataSources, score, featureScores);
    }

    private void setAggregatedFeatureDataSource(JSONObject event, String dataSource){
    	event.put(dataSourceFieldName, dataSource);
    }
    public String getAggregatedFeatureDataSource(JSONObject event){
    	return event.getAsString(dataSourceFieldName);
    }
    
    private void setAggregatedFeatureType(JSONObject event, String aggregatedFeatureType){
    	event.put(AggrEvent.EVENT_FIELD_FEATURE_TYPE, aggregatedFeatureType);
    }
    public String getAggregatedFeatureType(JSONObject event){
    	return event.getAsString(AggrEvent.EVENT_FIELD_FEATURE_TYPE);
    }
    
    private void setAggregatedFeatureName(JSONObject event, String aggregatedFeatureName){
    	event.put(aggrFeatureNameFieldName, aggregatedFeatureName);
    }
    public String getAggregatedFeatureName(JSONObject event){
    	return event.getAsString(aggrFeatureNameFieldName);
    }
    
    private void setAggregatedFeatureValue(JSONObject event, Double aggregatedFeatureValue){
    	event.put(aggrFeatureValueFieldName, aggregatedFeatureValue);
    }
    public Double getAggregatedFeatureValue(JSONObject event){
    	return ConversionUtils.convertToDouble(event.get(aggrFeatureValueFieldName));
    }
    
    private void setAggregatedFeatureAdditionalInfo(JSONObject event, Map<String, Object> additionalInfoMap){
    	if(additionalInfoMap!=null) {
			event.put(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_INFO, new JSONObject(additionalInfoMap));
		}
    }
    @SuppressWarnings("unchecked")
	private Map<String, Object> getAggregatedFeatureAdditionalInfo(JSONObject event){
		return (Map<String, Object>) event.get(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_INFO);
    }
    
    private void setAggregatedFeatureBucketConfName(JSONObject event, String bucketConfName){
    	event.put(bucketConfNameFieldName, bucketConfName);
    }
    public String getAggregatedFeatureBucketConfName(JSONObject event){
    	return event.getAsString(bucketConfNameFieldName);
    }
    
    private void setAggregatedFeatureContext(JSONObject event, Map<String, String> context){
    	event.put(contextFieldName, context);
    }
    @SuppressWarnings("unchecked")
    public Map<String, String> getAggregatedFeatureContext(JSONObject event){
    	return (Map<String, String>)event.get(contextFieldName);
    }

    public String getAggregatedFeatureContextId(JSONObject event) {
        return getAggregatedFeatureContextId(getAggregatedFeatureContext(event));
    }

    private void setAggregatedFeatureCreationEpochTime(JSONObject event, Long creationEpochTime){
    	event.put(AggrEvent.EVENT_FIELD_CREATION_EPOCHTIME, creationEpochTime);
    }
    public Long getAggregatedFeatureCreationEpochTime(JSONObject event){
    	return event.getAsNumber(AggrEvent.EVENT_FIELD_CREATION_EPOCHTIME).longValue();
    }
    
    private void setAggregatedFeatureStartTimeUnix(JSONObject event, Long startTimeUnix){
    	event.put(AggrEvent.EVENT_FIELD_START_TIME_UNIX, startTimeUnix);
    }
    public Long getAggregatedFeatureStartTimeUnix(JSONObject event){
    	return event.getAsNumber(AggrEvent.EVENT_FIELD_START_TIME_UNIX).longValue();
    }
    
    private void setAggregatedFeatureEndTimeUnix(JSONObject event, Long endTimeUnix){
    	event.put(AggrEvent.EVENT_FIELD_END_TIME_UNIX, endTimeUnix);
    }
    public Long getAggregatedFeatureEndTimeUnix(JSONObject event){
    	return event.getAsNumber(AggrEvent.EVENT_FIELD_END_TIME_UNIX).longValue();
    }
    
    private void setAggregatedFeatureDataSources(JSONObject event, List<String> dataSources){
    	JSONArray dataSourcesJsonArray = new JSONArray();
		dataSourcesJsonArray.addAll(dataSources);
		event.put(AggrEvent.EVENT_FIELD_DATA_SOURCES, dataSourcesJsonArray);
    }
    @SuppressWarnings("unchecked")
    public List<String> getAggregatedFeatureDataSources(JSONObject event){
    	return (List<String>) event.get(AggrEvent.EVENT_FIELD_DATA_SOURCES);
    }
    
    public void setAggregatedFeatureScore(JSONObject event, Double aggregatedFeatureScore){
    	event.put(AggrEvent.EVENT_FIELD_SCORE, aggregatedFeatureScore);
    }
    public Double getAggregatedFeatureScore(JSONObject event){
    	return ConversionUtils.convertToDouble(event.get(AggrEvent.EVENT_FIELD_SCORE));
    }

	public List<FeatureScore> getFeatureScores(JSONObject event){
		return (List<FeatureScore>) event.get(AggrEvent.EVENT_FIELD_FEATURE_SCORES);
	}

    public static String getAggregatedFeatureContextId(Map<String, String> context) {
        return context.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> StringUtils.join(entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()))
                .collect(Collectors.joining(CONTEXT_ID_SEPARATOR));
    }
}
