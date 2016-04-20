package fortscale.aggregation.feature.event.batch;

import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.event.*;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AggrFeatureEventBatchService {
    private static final Logger logger = Logger.getLogger(AggrFeatureEventBatchService.class);

    @Value("${fortscale.aggregation.batch.bucket.retrieving.page.size}")
    private int bucketsRetrievingPageSize;
    @Value("${fortscale.aggregation.batch.feature.event.to.send.save.page.size}")
    private int eventToSendSavePageSize;
    @Value("${fortscale.aggregation.batch.feature.event.to.send.retrieving.page.size}")
    private int eventToSendRetrievingPageSize;

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;

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
        List<AggrFeatureEventToSend> aggrFeatureEventToSendList = new ArrayList<>();
        for(FeatureBucketConf featureBucketConf: bucketConfigurationService.getFeatureBucketConfs()){
            int i = 0;
            List<FeatureBucket> featureBuckets = null;
            do {
                PageRequest pageRequest = new PageRequest(i, bucketsRetrievingPageSize);
                featureBuckets = featureBucketsReaderService.getFeatureBucketsByTimeRange(featureBucketConf, bucketStartTime, bucketEndTime, pageRequest);
                for (FeatureBucket bucket : featureBuckets) {
                    build(bucket, aggrFeatureEventToSendList);
                    if(aggrFeatureEventToSendList.size() >= eventToSendSavePageSize){
                        aggrFeatureEventToSendRepository.save(aggrFeatureEventToSendList);
                        aggrFeatureEventToSendList = new ArrayList<>();
                    }
                }

                i++;
            }while(featureBuckets.size() == bucketsRetrievingPageSize);
        }

        if(aggrFeatureEventToSendList.size() > 0){
            aggrFeatureEventToSendRepository.save(aggrFeatureEventToSendList);
        }

        sendEvents(sender, bucketStartTime, bucketEndTime);
    }

    private void build(FeatureBucket bucket, List<AggrFeatureEventToSend> aggrFeatureEventToSendList){
        List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();
        bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
        for (AggregatedFeatureEventConf conf : aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList(bucket.getFeatureBucketConfName())) {
            Feature feature = aggrFeatureEventFunctionsService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);
            AggrFeatureEventToSend aggrFeatureEventToSend = new AggrFeatureEventToSend(bucket.getBucketId(), conf.getName(), bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());
            aggrFeatureEventToSendList.add(aggrFeatureEventToSend);
        }
    }

    public void sendEvents(IAggregationEventSender sender, Long bucketStartTime, Long bucketEndTime){
        logger.info("Sending aggregated feature events...");
        if(sender != null) {
            int i = 0;
            List<AggrFeatureEventToSend> aggrFeatureEventToSendList = null;
            do{
                PageRequest pageRequest = new PageRequest(i, eventToSendRetrievingPageSize, Sort.Direction.ASC, AggrFeatureEventToSend.END_TIME_FIELD);
                aggrFeatureEventToSendList = aggrFeatureEventToSendRepository.findByEndTimeBetween(bucketStartTime, bucketEndTime, pageRequest);
                for (AggrFeatureEventToSend aggrFeatureEventToSend : aggrFeatureEventToSendList){
                    sendEvent(sender, aggrFeatureEventToSend);
                }
                i++;
                logger.info(String.format("Sent %d aggregated feature events", aggrFeatureEventToSendList.size()));
            } while(aggrFeatureEventToSendList.size() == eventToSendRetrievingPageSize);
            logger.info("Finished to send events");
        }
    }



    private void sendEvent(IAggregationEventSender sender, AggrFeatureEventToSend aggrFeatureEventToSend){
        AggregatedFeatureEventConf conf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(aggrFeatureEventToSend.getAggregatedFeatureEventConfName());
        if(conf == null){
            logger.warn("no aggregation conf for {}", aggrFeatureEventToSend.getAggregatedFeatureEventConfName());
        }
        JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, aggrFeatureEventToSend.getContext(), aggrFeatureEventToSend.getFeature(), aggrFeatureEventToSend.getStartTime(), aggrFeatureEventToSend.getEndTime());

        boolean isOfTypeF = AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE.equals(conf.getType());
        sender.send(isOfTypeF, event);
    }

    public void deleteEvents(Long bucketStartTime, Long bucketEndTime){
        aggrFeatureEventToSendRepository.deleteByEndTimeBetween(bucketStartTime, bucketEndTime);
    }

    public void deleteAllEvents(){
        aggrFeatureEventToSendRepository.deleteAll();
    }
}
