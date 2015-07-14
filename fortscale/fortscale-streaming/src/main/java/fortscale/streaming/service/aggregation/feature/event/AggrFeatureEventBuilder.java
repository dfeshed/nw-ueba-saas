package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.streaming.service.aggregation.*;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by amira on 08/07/2015.
 */
public class AggrFeatureEventBuilder {

    private static final String EVENT_FIELD_BUCKET_CONF_NAME = "bucket_conf_name";
    private static final String EVENT_FIELD_DATE_TIME_UNIX = "date_time_unix";
    private static final String EVENT_FIELD_DATE_TIME = "date_time";
    private static final String EVENT_FIELD_CONTEXT = "context";
    private static final String EVENT_FIELD_EVENT_TYPE = "event_type";
    private static final Object AGGREGATED_FEATURE_EVENT = "aggregated_feature_event";

    private AggrFeatureEventConf conf;
    private FeatureBucketStrategy bucketStrategy;
    private AggrFeatureEventService aggrFeatureEventService;
    private Map<Map<String, String>, AggrFeatureEventData> context2featureDataMap;
    private Map<String, AggrFeatureEventData> bucktID2featureDataMap;

    @Autowired
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Autowired
    private FeatureBucketsService featureBucketsService;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @Autowired
    AggrEventTopologyService aggrEventTopologyService;

    AggrFeatureEventBuilder(AggrFeatureEventConf conf, FeatureBucketStrategy bucketStrategy, AggrFeatureEventService aggrFeatureEventService) {
        this.conf = conf;
        this.bucketStrategy = bucketStrategy;
        this.aggrFeatureEventService = aggrFeatureEventService;
        context2featureDataMap = new HashMap<>();
        bucktID2featureDataMap = new HashMap<>();
    }

    void updateAggrFeatureEvent(String bucketID, Map<String, String> context, long startTime, long endTime) {
        AggrFeatureEventData eventData = context2featureDataMap.get(context);
        if(eventData==null) {
            eventData = new AggrFeatureEventData(this, context, conf.getBucketLeap());
            context2featureDataMap.put(context, eventData);
        }

        eventData.addBucketID(bucketID, startTime, endTime);
        bucktID2featureDataMap.put(bucketID, eventData);

        List<String> dataSources = conf.getFeatureBucketConf().getDataSources();
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
            List<AggrFeatureEventData.BucketData> bucketIDs = aggrFeatureEventData.getBucketIDs();
            int i = bucketIDs.size() - conf.getNumberOfBuckets();

            /*
            * TODO: handle the case when there are less then numberOfBuckets in the list
            * currently if there are not enough buckets in the list we do not create an event.
            * When we'll have support for historical 'bucket ticks' then we can use it to
            * retrieve the buckets from the bucket store by query instead of using the  EventData
            * bucketIDs list.
            * */
            if(bucketIDs.size()-i == conf.getNumberOfBuckets()) {
                //  Enough buckets in the list to create the event
                for (i = (i < 0) ? 0 : i; i < bucketIDs.size(); i++) {
                    AggrFeatureEventData.BucketData bucketData = bucketIDs.get(i);
                    String bucketID = bucketData.getBucketID();

                    // Creating an empty bucket & aggrFeatures map for the empty 'bucket tick' case
                    Map<String, Feature> aggrFeatures = new HashMap<>();
                    FeatureBucket bucket = null;

                    // If bucketID is null it means an empty 'bucket tick'
                    if (bucketID != null) {
                        bucket = featureBucketsService.getFeatureBucket(conf.getFeatureBucketConf(), bucketID);
                    }

                    if (bucket != null) {
                        aggrFeatures = bucket.getAggregatedFeatures();
                        if (i == 0) {
                            startTime = bucket.getStartTime();
                        }
                        if (i == bucketIDs.size() - 1) {
                            endTime = bucket.getEndTime();
                        }
                    } else { // Empty bucket tick
                        Assert.notNull(bucketData.getStrategyData());
                        if (i == 0) {
                            startTime = bucketData.getStrategyData().getStartTime();
                        }
                        if (i == bucketIDs.size() - 1) {
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
                    AggrFeatureEventData.BucketData bucketData = bucketIDs.remove(0);
                    if(bucketData.getBucketID()!=null) {
                        aggrFeatureEventService.removeBucketID2builderMapping(bucketData.getBucketID(), this);
                    }
                }

            } //if(bucketIDs.size()-i == conf.getNumberOfBuckets())

        } //if(aggrFeatureEventData.getNumberOfBucketsToWaitBeforeSendingNextEvent()<=0)

        // Registering in timer to be waked up on the next bucket end time
        FeatureBucketStrategyData featureBucketStrategyData = bucketStrategy.getNextBucketStrategyData(conf.getFeatureBucketConf(), aggrFeatureEventData.getContext());
        if(featureBucketStrategyData!=null) {
            aggrFeatureEventData.nextBucketEndTimeUpdate(featureBucketStrategyData);
        } else {
            bucketStrategy.notifyWhenNextBucketEndTimeIsKnown(conf.getFeatureBucketConf(), aggrFeatureEventData.getContext(), aggrFeatureEventData);
        }

    }

    void registerInTimerForNextBucketEndTime(AggrFeatureEventData aggrFeatureEventData, Long time) {
        if(aggrFeatureEventData!=null && time!=null) {
            long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(conf.getFeatureBucketConf().getDataSources(), time, aggrFeatureEventData);
            aggrFeatureEventData.setSyncTimerRegistrationID(registrationID);
        }
    }

    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }

    private JSONObject buildEvent(Map<String, String> context, Feature feature, Long startTime, Long endTime) {
        JSONObject event = new JSONObject();
        event.put(EVENT_FIELD_CONTEXT, context);
        event.put(EVENT_FIELD_EVENT_TYPE, AGGREGATED_FEATURE_EVENT);
        event.put(feature.getName(), feature.getValue());
        event.put(EVENT_FIELD_BUCKET_CONF_NAME, conf.getFeatureBucketConf().getName());
        Long date_time_unix = System.currentTimeMillis() / 1000;
        event.put(EVENT_FIELD_DATE_TIME_UNIX, date_time_unix);
        String date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        event.put(EVENT_FIELD_DATE_TIME, date_time);
        return event;
    }

}
