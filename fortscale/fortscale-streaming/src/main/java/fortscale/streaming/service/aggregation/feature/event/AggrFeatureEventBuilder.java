package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.streaming.service.aggregation.*;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by amira on 08/07/2015.
 */
@Configurable(preConstruction = true)
public class AggrFeatureEventBuilder {

    private static final String EVENT_FIELD_BUCKET_CONF_NAME = "bucket_conf_name";
    private static final String EVENT_FIELD_DATE_TIME_UNIX = "date_time_unix";
    private static final String EVENT_FIELD_DATE_TIME = "date_time";
    private static final String EVENT_FIELD_CONTEXT = "context";
    private static final String EVENT_FIELD_EVENT_TYPE = "event_type";
    private static final Object AGGREGATED_FEATURE_EVENT = "aggregated_feature_event";
    private static final String EVENT_FIELD_START_TIME_UNIX = "start_time_unix";
    private static final String EVENT_FIELD_START_TIME = "start_time";
    private static final String EVENT_FIELD_END_TIME_UNIX = "end_time_unix";
    private static final String EVENT_FIELD_END_TIME = "end_time";

    private AggregatedFeatureEventConf conf;
    private FeatureBucketStrategy bucketStrategy;
    private AggrFeatureEventService aggrFeatureEventService;
    private Map<Map<String, String>, AggrFeatureEventData> context2featureDataMap;
    private Map<String, AggrFeatureEventData> bucktID2featureDataMap;


    @Autowired
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @Autowired
    AggrEventTopologyService aggrEventTopologyService;

    private FeatureBucketsService featureBucketsService;

    AggrFeatureEventBuilder(AggregatedFeatureEventConf conf, FeatureBucketStrategy bucketStrategy, AggrFeatureEventService aggrFeatureEventService, FeatureBucketsService featureBucketsService) {
        this.conf = conf;
        this.bucketStrategy = bucketStrategy;
        this.aggrFeatureEventService = aggrFeatureEventService;
        this.featureBucketsService = featureBucketsService;
        context2featureDataMap = new HashMap<>();
        bucktID2featureDataMap = new HashMap<>();
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
     * @param bucketID
     * @param context
     * @param startTime
     * @param endTime
     */
    void updateAggrFeatureEventData(String bucketID, Map<String, String> context, long startTime, long endTime) {
        AggrFeatureEventData eventData = context2featureDataMap.get(context);
        if(eventData==null) {
            eventData = new AggrFeatureEventData(this, context, conf.getBucketsLeap());
            context2featureDataMap.put(context, eventData);
        }

        eventData.addBucketID(bucketID, startTime, endTime);
        bucktID2featureDataMap.put(bucketID, eventData);

        List<String> dataSources = conf.getBucketConf().getDataSources();
        long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(dataSources, endTime, eventData);
        eventData.setSyncTimerRegistrationID(registrationID);
    }

    void updateFeatureBacketEndTime(String bucketID, Long endTime) {
        if(bucketID!=null && StringUtils.isNotEmpty(bucketID)) {
            AggrFeatureEventData eventData = bucktID2featureDataMap.get(bucketID);
            if(eventData!=null) {
                eventData.setEndTime(bucketID, endTime);
                long newRegistrationID = dataSourcesSyncTimer.updateNotificationRegistration(eventData.getSyncTimerRegistrationID(), endTime);
                eventData.setSyncTimerRegistrationID(newRegistrationID);
            }
        }
    }

    public void dataSourcesReachedTime(AggrFeatureEventData aggrFeatureEventData) {
        List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();

        Long startTime=0L;
        Long endTime=0L;

        // Checking if need to create event based on bucketLeap
        if(aggrFeatureEventData.getNumberOfBucketsToWaitBeforeSendingNextEvent()<=0) {

            aggrFeatureEventData.updateNumberOfBucketsToWaitBeforeSendingNextEvent();

            // Retrieving the aggregated features from the latest 'numberOfBuckets'
            //TODO: need to change the code to take only those buckets that are older then the bucket that is being
            // closed right now.
            //TODO: ask the strategy for the next bucket only if closing the last bucket.
            List<AggrFeatureEventData.BucketData> bucketIDs = aggrFeatureEventData.getBucketIDs();
            int i = bucketIDs.size() - conf.getNumberOfBuckets();

            /*
            * TODO: handle the case when there are less then numberOfBuckets in the list
            * currently if there are not enough buckets in the list we do not create an event.
            * When we'll have support for historical 'bucket ticks' then we can use it to
            * retrieve the buckets from the bucket store by query instead of using the  EventData
            * bucketIDs list.
            * */
            if(i >= 0) {
                int firstBucketIndex = i;
                int lastBucketIndex = i+conf.getNumberOfBuckets()-1;
                //  Enough buckets in the list to create the event
                for (; i < bucketIDs.size(); i++) {
                    AggrFeatureEventData.BucketData bucketData = bucketIDs.get(i);
                    String bucketID = bucketData.getBucketID();

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
                        if (i == lastBucketIndex) {
                            endTime = bucket.getEndTime();
                        }
                    } else { // Empty bucket tick
                        Assert.notNull(bucketData.getStrategyData());
                        if (i == firstBucketIndex) {
                            startTime = bucketData.getStrategyData().getStartTime();
                        }
                        if (i == lastBucketIndex) {
                            endTime = bucketData.getStrategyData().getEndTime();
                        }
                    }
                    bucketAggrFeaturesMapList.add(aggrFeatures);
                }

                // Calculating the new feature
                Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);

                // Building the event
                JSONObject event = buildEvent(aggrFeatureEventData.getContext(), feature, startTime, endTime);

                // Sending the event
                sendEvent(event);

                // Cleaning old buckets
                int howManyToRemove =  bucketIDs.size() - conf.getNumberOfBuckets();
                for ( i=0; i < howManyToRemove; i++) {
                    AggrFeatureEventData.BucketData bucketData = aggrFeatureEventData.removeFirstBucketData();
                    if(bucketData.getBucketID()!=null) {
                        aggrFeatureEventService.removeBucketID2builderMapping(bucketData.getBucketID(), this);
                    }
                }

            } //if(bucketIDs.size()-i == conf.getNumberOfBuckets())

        } //if(aggrFeatureEventData.getNumberOfBucketsToWaitBeforeSendingNextEvent()<=0)

        // Registering in timer to be waked up on the next bucket end time
        // TODO: add logic to stop registering for next bucket updates if there are X number of empty buckets
        FeatureBucketStrategyData featureBucketStrategyData = bucketStrategy.getNextBucketStrategyData(conf.getBucketConf(), aggrFeatureEventData.getContext(), endTime);
        if(featureBucketStrategyData!=null) {
            aggrFeatureEventData.nextBucketEndTimeUpdate(featureBucketStrategyData);
        } else {
            bucketStrategy.notifyWhenNextBucketEndTimeIsKnown(conf.getBucketConf(), aggrFeatureEventData.getContext(), aggrFeatureEventData, endTime);
        }

    }

    void registerInTimerForNextBucketEndTime(AggrFeatureEventData aggrFeatureEventData, Long time) {
        if(aggrFeatureEventData!=null && time!=null) {
            long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(conf.getBucketConf().getDataSources(), time, aggrFeatureEventData);
            aggrFeatureEventData.setSyncTimerRegistrationID(registrationID);
        }
    }

    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }

    /**
     * Builds an event in the following format:
     * <pre>
     *     {
     *        "event_type": "aggregated_feature_event",
     *        "aggregated feature name": "the value",
     *        "bucket_conf_name": "the BucketConf name from which this aggrFeature was created",
     *
     *        "date_time_unix": 1430460833,
     *        "date_time": "2015-05-01 06:13:53",
     *
     *        "start_time_unix": 1430460833,
     *        "start_time": "2015-05-01 06:13:53",
     *
     *        "end_time_unix": 1430460833,
     *        "end_time": "2015-05-01 06:13:53",
     *
     *        "context": {
     *          // Context fields, e.g.:
     *          "user" : "John Smith",
     *          "machine": "m1"
     *        }
     *      }
     *</pre>
     * @param context
     * @param feature
     * @param startTimeSec
     * @param endTimeSec
     * @return the event as JSONObject
     */
    private JSONObject buildEvent(Map<String, String> context, Feature feature, Long startTimeSec, Long endTimeSec) {
        JSONObject event = new JSONObject();
        event.put(EVENT_FIELD_CONTEXT, context);
        event.put(EVENT_FIELD_EVENT_TYPE, AGGREGATED_FEATURE_EVENT);
        event.put(feature.getName(), feature.getValue());
        event.put(EVENT_FIELD_BUCKET_CONF_NAME, conf.getBucketConfName());

        // Event time
        Long date_time_unix = System.currentTimeMillis() / 1000;
        event.put(EVENT_FIELD_DATE_TIME_UNIX, date_time_unix);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
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

        return event;
    }

}
