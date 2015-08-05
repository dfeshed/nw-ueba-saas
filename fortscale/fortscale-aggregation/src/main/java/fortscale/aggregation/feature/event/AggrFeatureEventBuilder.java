package fortscale.aggregation.feature.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategy;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.functions.AggrFeatureValue;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;

@Configurable(preConstruction = true)
public class AggrFeatureEventBuilder {

    public static final String EVENT_FIELD_DATE_TIME_UNIX = "date_time_unix";
    protected static final String EVENT_FIELD_DATE_TIME = "date_time";
    public static final String EVENT_FIELD_CONTEXT = "context";
    public static final String EVENT_FIELD_FEATURE_TYPE = "aggregated_feature_type";
    public static final String EVENT_FIELD_START_TIME_UNIX = "start_time_unix";
    protected static final String EVENT_FIELD_START_TIME = "start_time";
    public static final String EVENT_FIELD_END_TIME_UNIX = "end_time_unix";
    protected static final String EVENT_FIELD_END_TIME = "end_time";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_VALUE = "aggregated_feature_value";
    public static final String EVENT_FIELD_AGGREGATED_FEATURE_INFO = "aggregated_feature_info";
    public static final String EVENT_FIELD_DATA_SOURCES = "data_sources";
    
    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;

    private static final SimpleDateFormat format = getSimpleDateFormat();

    private static SimpleDateFormat getSimpleDateFormat(){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return format;
    }

    private AggregatedFeatureEventConf conf;
    private FeatureBucketStrategy bucketStrategy;
    private Map<EventContextData, AggrFeatureEventData> bucketAndStrategyContexts2eventDataMap;
    private Map<String, AggrFeatureEventData> bucktID2eventDataMap;

    @Value("${fetch.data.cycle.in.seconds}")
    private long fetchDataCycleInSeconds;

    @Autowired
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @Autowired
    AggrEventTopologyService aggrEventTopologyService;

    private FeatureBucketsService featureBucketsService;


    AggrFeatureEventBuilder(AggregatedFeatureEventConf conf, FeatureBucketStrategy bucketStrategy, FeatureBucketsService featureBucketsService) {
        this.conf = conf;
        this.bucketStrategy = bucketStrategy;
        this.featureBucketsService = featureBucketsService;
        bucketAndStrategyContexts2eventDataMap = new HashMap<>();
        bucktID2eventDataMap = new HashMap<>();
    }

    /*
        * Should be used only by Unit Tests.
         */
    void setFeatureBucketsService(FeatureBucketsService featureBucketsService) {
        this.featureBucketsService = featureBucketsService;
    }

    /*
    * Should be used only by Unit Tests.
     */
    void setAggrFeatureFuncService(IAggrFeatureEventFunctionsService aggrFeatureFuncService) {
        this.aggrFeatureFuncService = aggrFeatureFuncService;
    }

    /*
    * Should be used only by Unit Tests.
     */
    void setAggrEventTopologyService(AggrEventTopologyService aggrEventTopologyService) {
        this.aggrEventTopologyService = aggrEventTopologyService;
    }

    /*
    * Should be used only by Unit Tests.
     */
    public void setDataSourcesSyncTimer(DataSourcesSyncTimer dataSourcesSyncTimer) {
        this.dataSourcesSyncTimer = dataSourcesSyncTimer;
    }

    /**
     * Updates the eventData related to the given context, or creates a new evenData if not exists.
     */
    void updateAggrFeatureEventData(String bucketID, String strategyId, Map<String, String> context, long startTime, long endTime) {
        Assert.notNull(bucketID);
        Assert.isTrue(StringUtils.isNotEmpty(bucketID));
        Assert.notNull(strategyId);
        Assert.isTrue(StringUtils.isNotEmpty(strategyId));
        Assert.notNull(context);
        Assert.notEmpty(context);
        Assert.isTrue(endTime > startTime && startTime > 946684800); //01 Jan 2000 00:00:00 GMT

        String startegyContextId = bucketStrategy.getStrategyContextIdFromStrategyId(strategyId);

        EventContextData eventContextData = new EventContextData(context, startegyContextId);
        AggrFeatureEventData eventData = bucketAndStrategyContexts2eventDataMap.get(eventContextData);
        if(eventData==null) {
            eventData = new AggrFeatureEventData(this, context, strategyId);
            bucketAndStrategyContexts2eventDataMap.put(eventContextData, eventData);
        }

        AggrFeatureEventData.BucketTick bucketTick =  eventData.addBucketID(bucketID, startTime, endTime);
        bucktID2eventDataMap.put(bucketID, eventData);

        registerInTimerForNextBucketEndTime(bucketTick, endTime);
    }

    void updateFeatureBacketEndTime(String bucketID, Long endTime) {
        if(bucketID!=null && StringUtils.isNotEmpty(bucketID)) {
            AggrFeatureEventData eventData = bucktID2eventDataMap.get(bucketID);
            if(eventData!=null) {
                AggrFeatureEventData.BucketTick bucketTick = eventData.setEndTime(bucketID, endTime);
                long newRegistrationID = dataSourcesSyncTimer.updateNotificationRegistration(bucketTick.getSyncTimerRegistrationID(), endTime);
                bucketTick.setSyncTimerRegistrationID(newRegistrationID);
            }
        }
    }

    public void dataSourcesReachedTime(AggrFeatureEventData aggrFeatureEventData, AggrFeatureEventData.BucketTick wakedBucketTick) {
        List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();

        Long startTime=0L;

        // Retrieving the aggregated features from the latest 'numberOfBuckets'
        //TODO: need to change the code to take only those buckets that are older then the bucket that is being
        // closed right now.
        //TODO: ask the strategy for the next bucket only if closing the last bucket.
        List<AggrFeatureEventData.BucketTick> bucketIDs = aggrFeatureEventData.getBucketTicks();

        /*
        * TODO: handle the case when there are less then numberOfBuckets in the list
        * currently if there are not enough buckets in the list we do not create an event.
        * When we'll have support for historical 'bucket ticks' then we can use it to
        * retrieve the buckets from the bucket store by query instead of using the  EventData
        * bucketIDs list.
        * */

        int wakedBucketDataIndex = bucketIDs.indexOf(wakedBucketTick);

        boolean leapTurn = aggrFeatureEventData.doesItMatchBucketLeap(wakedBucketTick);

        if(  wakedBucketDataIndex >=0 && leapTurn ) {

            wakedBucketTick.setProcessedAsLeadingBucket(true);

            if(wakedBucketDataIndex >= (conf.getNumberOfBuckets()-1))   //  Enough buckets in the list to create the event
            {

                aggrFeatureEventData.setLastSentEventBucketTick(wakedBucketTick);
                int firstBucketIndex = wakedBucketDataIndex - conf.getNumberOfBuckets() + 1;
                int i = firstBucketIndex;

                for (; i <= wakedBucketDataIndex; i++) {
                    AggrFeatureEventData.BucketTick bucketTick = bucketIDs.get(i);
                    String bucketID = bucketTick.getBucketId();

                    // Creating an empty bucket & aggrFeatures map for the empty 'bucket tick' case
                    Map<String, Feature> aggrFeatures = new HashMap<>();
                    FeatureBucket bucket = null;

                    // If bucketID is null it means an empty 'bucket tick'
                    if (bucketID != null) {
                        bucket = featureBucketsService.getFeatureBucket(conf.getBucketConf(), bucketID);
                    }

                    if (bucket != null) {
                        aggrFeatures = bucket.getAggregatedFeatures();
                        if (i == firstBucketIndex) {
                            startTime = bucket.getStartTime();
                        }
                    } else { // Empty bucket tick
                        Assert.notNull(bucketTick.getStrategyData());
                        if (i == firstBucketIndex) {
                            startTime = bucketTick.getStrategyData().getStartTime();
                        }
                    }
                    bucketAggrFeaturesMapList.add(aggrFeatures);
                }
                // Calculating the new feature
                Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);

                // Building the event
                JSONObject event = buildEvent(aggrFeatureEventData.getContext(), feature, startTime, wakedBucketTick.getEndTime());

                // Sending the event
                sendEvent(event);

                aggrFeatureEventData.clearOldBucketData();
            }
        }

        // Registering in timer to be waked up on the next bucket end time
        // Only if the waked bucket is the last bucket in the list
        if(wakedBucketDataIndex == bucketIDs.size()-1) {
            FeatureBucketStrategyData featureBucketStrategyData = bucketStrategy.getNextBucketStrategyData(conf.getBucketConf(), aggrFeatureEventData.getFirstBucketStrategyId(), wakedBucketTick.getEndTime());
            if (featureBucketStrategyData != null) {
                aggrFeatureEventData.nextBucketEndTimeUpdate(featureBucketStrategyData);
            } else {
                bucketStrategy.notifyWhenNextBucketEndTimeIsKnown(conf.getBucketConf(), aggrFeatureEventData.getFirstBucketStrategyId(), aggrFeatureEventData, wakedBucketTick.getEndTime());
            }
        }
    }

    void registerInTimerForNextBucketEndTime(AggrFeatureEventData.BucketTick bucketTick, Long time) {
        if(bucketTick !=null && time!=null) {
            long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(conf.getBucketConf().getDataSources(), time + fetchDataCycleInSeconds, bucketTick);
            bucketTick.setSyncTimerRegistrationID(registrationID);
        }
    }

    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }

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
    private JSONObject buildEvent(Map<String, String> context, Feature feature, Long startTimeSec, Long endTimeSec) throws IllegalArgumentException{
        AggrFeatureValue featureValue;
        Object value;
        Map<String, Object> additionalInfoMap;

        try {
            featureValue = (AggrFeatureValue)feature.getValue();
            value = featureValue.getValue();
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Feature is null or value is null or value is not a AggrFeatureValue object: %s", feature), ex);
        }
        if(value==null) {
            throw new IllegalArgumentException(String.format("Feature value doesn't contain a 'value' element: %s", featureValue));
        }
        additionalInfoMap = featureValue.getAdditionalInformationMap();

        JSONObject event = new JSONObject();
        //static data for all aggregated feature events
        event.put(eventTypeFieldName, eventTypeFieldValue);

        // Feature Data
        event.put(EVENT_FIELD_FEATURE_TYPE, conf.getType());
        event.put(aggrFeatureNameFieldName, conf.getName());
        event.put(EVENT_FIELD_AGGREGATED_FEATURE_VALUE, value);
        if(additionalInfoMap!=null) {
            event.put(EVENT_FIELD_AGGREGATED_FEATURE_INFO, new JSONObject(additionalInfoMap));
        }
        event.put(bucketConfNameFieldName, conf.getBucketConfName());

        // Context
        event.put(EVENT_FIELD_CONTEXT, context);

        // Event time
        Long date_time_unix = System.currentTimeMillis() / 1000;
        event.put(EVENT_FIELD_DATE_TIME_UNIX, date_time_unix);

        String date_time = format.format(new Date(date_time_unix * 1000));
        event.put(EVENT_FIELD_DATE_TIME, date_time);

        // Start Time
        event.put(EVENT_FIELD_START_TIME_UNIX, startTimeSec);
        String start_time = format.format(new Date(startTimeSec * 1000));
        event.put(EVENT_FIELD_START_TIME, start_time);

        // End Time
        event.put(EVENT_FIELD_END_TIME_UNIX, endTimeSec);
        String end_time = format.format(new Date(endTimeSec * 1000));
        event.put(EVENT_FIELD_END_TIME, end_time);

        // Data Sources
        JSONArray dataSourcesJsonArray = new JSONArray();
        dataSourcesJsonArray.addAll(conf.getBucketConf().getDataSources());
        event.put(EVENT_FIELD_DATA_SOURCES, dataSourcesJsonArray);

        return event;
    }

    AggregatedFeatureEventConf getConf() {
        return conf;
    }

    public FeatureBucketStrategy getBucketStrategy() {
        return bucketStrategy;
    }

    void removeBucketId2eventDataMapping(String bucketId) {
        bucktID2eventDataMap.remove(bucketId);
    }

    private static class EventContextData {
        private Map<String, String> bucketContext;
        private String strategyContextId;

        public EventContextData(Map<String, String> bucketContext, String strategyContextId) {
            this.bucketContext = bucketContext;
            this.strategyContextId = strategyContextId;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventContextData that = (EventContextData) o;

            return new EqualsBuilder()
                    .append(bucketContext, that.bucketContext)
                    .append(strategyContextId, that.strategyContextId)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(449, 43).
                    append(bucketContext).
                    toHashCode();
        }
    }
}
