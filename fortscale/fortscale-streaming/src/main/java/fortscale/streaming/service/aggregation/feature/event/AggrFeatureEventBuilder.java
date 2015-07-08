package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.functions.AggrFeatureFuncService;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.streaming.aggregation.feature.functions.IAggrFeatureFunctionsService;
import fortscale.streaming.service.aggregation.DataSourcesSyncTimer;
import fortscale.streaming.service.aggregation.FeatureBucket;
import fortscale.streaming.service.aggregation.FeatureBucketsStore;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategy;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by amira on 08/07/2015.
 */
public class AggrFeatureEventBuilder {
    private AggrFeatureEventConf conf;
    private FeatureBucketStrategy bucketStrategy;
    private AggrFeatureEventService aggrFeatureEventService;
    private Map<Map<String, String>, AggrFeatureEventData> context2featureDataMap;
    private Map<String, AggrFeatureEventData> bucktID2featureDataMap;

    @Autowired
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Autowired
    private FeatureBucketsStore featureBucketsStore;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

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
            eventData = new AggrFeatureEventData(this, context, startTime, endTime);
            context2featureDataMap.put(context, eventData);
        }

        eventData.addBucketID(bucketID);
        bucktID2featureDataMap.put(bucketID, eventData);

        List<String> dataSources = conf.getFeatureBucketConf().getDataSources();
        long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(dataSources, endTime, eventData);
        eventData.setSyncTimerRegistrationID(registrationID);
    }

    void updateFeatureBacketEndTime(String bucketID, Long endTime) {
        if(bucketID!=null && StringUtils.isNotEmpty(bucketID)) {
            AggrFeatureEventData eventData = bucktID2featureDataMap.get(bucketID);
            if(eventData!=null) {
                eventData.setEndTime(endTime);
                long newRegistrationID = dataSourcesSyncTimer.updateNotificationRegistration(eventData.getSyncTimerRegistrationID(), endTime);
                eventData.setSyncTimerRegistrationID(newRegistrationID);
            }
        }
    }

    public void dataSourcesReachedTime(AggrFeatureEventData aggrFeatureEventData) {
        List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();

        List<String> bucketIDs = aggrFeatureEventData.getBucketIDs();
        int i = bucketIDs.size()-conf.getNumberOfBuckets();
        if(i<0) {
            i=0;
        }
        for ( ; i < bucketIDs.size(); i++) {
            String bucketID =  bucketIDs.get(i);
            FeatureBucket bucket = featureBucketsStore.getFeatureBucket(conf.getFeatureBucketConf(), bucketID);
            Map<String, Feature> aggrFeatures = bucket.getAggregatedFeatures();
            bucketAggrFeaturesMapList.add(aggrFeatures);
        }

        Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);

        JSONObject event = buildEvent(feature);

        sendEvent(event);

        // Registering in timer to be waked up on the next bucket end time
        Long nextBucketEndTime = bucketStrategy.getNextBucketEndTime(aggrFeatureEventData.getContext());
        if(nextBucketEndTime==null) {
            bucketStrategy.notifyWhenNextBucketEndTimeIsKnown(aggrFeatureEventData);
            registerInTimerForNextBucketEndTime(aggrFeatureEventData, nextBucketEndTime);
        } else {
            bucketStrategy.notifyWhenNextBucketEndTimeIsKnown(aggrFeatureEventData);
        }
        //TODO
    }

    void registerInTimerForNextBucketEndTime(AggrFeatureEventData aggrFeatureEventData, Long time) {
        if(aggrFeatureEventData!=null && time!=null) {
            aggrFeatureEventData.setEndTime(time);
            long registrationID = dataSourcesSyncTimer.notifyWhenDataSourcesReachTime(conf.getFeatureBucketConf().getDataSources(), time, aggrFeatureEventData);
            aggrFeatureEventData.setSyncTimerRegistrationID(registrationID);
        }
    }

    private void sendEvent(JSONObject event) {
        //TODO
    }

    private JSONObject buildEvent(Feature feature) {
        //TODO
        return null;
    }
}
