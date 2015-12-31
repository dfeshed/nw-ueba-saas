package fortscale.aggregation.feature.event.batch;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.*;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AggrFeatureEventBatchService {

    private static final int DEFAULT_PAGE_SIZE = 1000;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;


    @Autowired
    private FeatureBucketsReaderService featureBucketsReaderService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private AggrFeatureEventToSendRepository aggrFeatureEventToSendRepository;


    public void buildAndSave(IAggregationEventSender sender, Long bucketStartTime, Long bucketEndTime){
        for(FeatureBucketConf featureBucketConf: bucketConfigurationService.getFeatureBucketConfs()){
            int i = 0;
            List<FeatureBucket> featureBuckets = null;
            do {
                PageRequest pageRequest = new PageRequest(i, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, FeatureBucket.START_TIME_FIELD);
                featureBuckets = featureBucketsReaderService.getFeatureBucketsByTimeRange(featureBucketConf, bucketStartTime, bucketEndTime, pageRequest);
                for (FeatureBucket bucket : featureBuckets) {
                    buildAndSave(bucket);
                }
            }while(featureBuckets.size() == DEFAULT_PAGE_SIZE);
        }

        sendEvents(sender, bucketStartTime, bucketEndTime);
    }

    private void buildAndSave(FeatureBucket bucket){
        List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();
        bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
        for (AggregatedFeatureEventConf conf : aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList(bucket.getFeatureBucketConfName())) {
            Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);
            saveEvent(conf, bucket, feature);
        }
    }

    private void saveEvent(AggregatedFeatureEventConf conf, FeatureBucket bucket, Feature feature){
        AggrFeatureEventToSend aggrFeatureEventToSend = new AggrFeatureEventToSend(bucket.getBucketId(), conf.getName(), bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());
    }

    public void sendEvents(IAggregationEventSender sender, Long bucketStartTime, Long bucketEndTime){
        if(sender != null) {
            int i = 0;
            List<AggrFeatureEventToSend> aggrFeatureEventToSendList = null;
            do{
                PageRequest pageRequest = new PageRequest(i, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, AggrFeatureEventToSend.START_TIME_FIELD);
                aggrFeatureEventToSendList = aggrFeatureEventToSendRepository.findByEndTimeGtAndEndTimeLte(bucketStartTime, bucketEndTime, pageRequest);
                for (AggrFeatureEventToSend aggrFeatureEventToSend: aggrFeatureEventToSendList){
                    sendEvent(sender, aggrFeatureEventToSend);
                }
                i++;
            } while(aggrFeatureEventToSendList.size() == DEFAULT_PAGE_SIZE);
        }
    }



    private void sendEvent(IAggregationEventSender sender, AggrFeatureEventToSend aggrFeatureEventToSend){
        AggregatedFeatureEventConf conf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(aggrFeatureEventToSend.getAggregatedFeatureEventConfName());
        JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, aggrFeatureEventToSend.getContext(), aggrFeatureEventToSend.getFeature(), aggrFeatureEventToSend.getStartTime(), aggrFeatureEventToSend.getEndTime());

        boolean isOfTypeF = AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE.equals(conf.getType());
        sender.send(isOfTypeF, event);
    }

    public void deleteEvents(Long bucketStartTime, Long bucketEndTime){
        aggrFeatureEventToSendRepository.deleteByEndTimeGtAndEndTimeLte(bucketStartTime, bucketEndTime);
    }

    public void deleteAllEvents(){
        aggrFeatureEventToSendRepository.deleteAll();
    }
}
