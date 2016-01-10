package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

import fortscale.aggregation.domain.feature.event.FeatureBucketAggrMetadata;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrMetadataRepository;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrSendingQueue;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrSendingQueueRepository;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

@Configurable(preConstruction=true)
public class AggrFeatureEventImprovedService implements IAggrFeatureEventService{
    private static final Logger logger = Logger.getLogger(AggrFeatureEventImprovedService.class);
    private static final String INFO_MSG_NO_EVENT_CONFS = "No aggregated feature event definitions were received.";
    
    protected static final long SECONDS_TO_ADD_TO_PASS_END_TIME = 1;

    @Value("${fetch.data.cycle.in.seconds}")
    private long fetchDataCycleInSeconds;
    @Value("${fortscale.aggregation.sync.timer.waiting.time.before.notification}")
	private long waitingTimeBeforeNotification;
    @Value("${fortscale.aggregation.sender.use.end.time.sort:true}")
	private boolean useEndTimeSort;
    
    @Autowired
    private FeatureBucketAggrMetadataRepository featureBucketAggrMetadataRepository;
    
    @Autowired 
    private FeatureBucketAggrSendingQueueRepository featureBucketAggrSendingQueueRepository; 
    
    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureFuncService;
    
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    
    @Autowired
    AggrEventTopologyService aggrEventTopologyService;

    private FeatureBucketsService featureBucketsService;

	private AggregatedFeatureEventsConfService aggrFeatureEventsConfService;

    public AggrFeatureEventImprovedService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketsService featureBucketsService) {
        Assert.notNull(aggrFeatureEventsConfService);
        Assert.notNull(featureBucketsService);

		this.featureBucketsService = featureBucketsService;
		this.aggrFeatureEventsConfService = aggrFeatureEventsConfService;
        List<AggregatedFeatureEventConf> aggrFeatureEventConfs = aggrFeatureEventsConfService.getAggregatedFeatureEventConfList();
        if(aggrFeatureEventConfs==null || aggrFeatureEventConfs.isEmpty()) {
            logger.info(INFO_MSG_NO_EVENT_CONFS);
        }
    }

    /**
     * Handling new feature buckets.
     * The assumption is that new buckets are coming in order.
     */
    public void newFeatureBuckets(List<FeatureBucket> buckets) {
    	List<FeatureBucketAggrMetadata> featureBucketAggrMetadataList = new ArrayList<>();
        for(FeatureBucket featureBucket : buckets) {
        	if(aggrFeatureEventsConfService.getAggregatedFeatureEventConfList(featureBucket.getFeatureBucketConfName()) != null){
	        	FeatureBucketAggrMetadata featureBucketAggrMetadata = new FeatureBucketAggrMetadata(featureBucket);
	        	featureBucketAggrMetadataList.add(featureBucketAggrMetadata);
        	}
        }

		featureBucketAggrMetadataRepository.save(featureBucketAggrMetadataList);
    }



    public void featureBucketsEndTimeUpdate(List<FeatureBucket> updatedFeatureBucketsWithNewEndTime) {
        if(updatedFeatureBucketsWithNewEndTime==null) {
            return;
        }
        
        for(FeatureBucket featureBucket : updatedFeatureBucketsWithNewEndTime) {
        	if(aggrFeatureEventsConfService.getAggregatedFeatureEventConfList(featureBucket.getFeatureBucketConfName()) != null){
				featureBucketAggrMetadataRepository.updateFeatureBucketsEndTime(featureBucket.getFeatureBucketConfName(), featureBucket.getBucketId(), featureBucket.getEndTime());
        	}
        }
    }
    
    public void sendEvents(long curEventTime){
    	//moving feature bucket to sending queue
    	long curTime = System.currentTimeMillis()/1000;
    	long endTime = curEventTime+fetchDataCycleInSeconds;
    	for(FeatureBucketAggrMetadata aggrMetadata: featureBucketAggrMetadataRepository.findByEndTimeLessThan(endTime)){
    		FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue = new FeatureBucketAggrSendingQueue(aggrMetadata.getFeatureBucketConfName(), aggrMetadata.getBucketId(), curTime, aggrMetadata.getEndTime());
    		featureBucketAggrSendingQueueRepository.save(featureBucketAggrSendingQueue);
    	}
		featureBucketAggrMetadataRepository.deleteByEndTimeLessThan(endTime);

    	//creating and sending feature aggregated events
    	List<FeatureBucketAggrSendingQueue> featureBucketAggrSendingQueueList = null;
    	long fireTime = curTime-waitingTimeBeforeNotification;
    	if(useEndTimeSort){
    		featureBucketAggrSendingQueueList = featureBucketAggrSendingQueueRepository.findByFireTimeLessThan(fireTime, new Sort(Direction.ASC, FeatureBucketAggrSendingQueue.END_TIME_FIELD));
		} else{
			featureBucketAggrSendingQueueList = featureBucketAggrSendingQueueRepository.findByFireTimeLessThan(fireTime);
		}
    	for(FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue: featureBucketAggrSendingQueueList){
    		FeatureBucket bucket = null;
    		List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();

    		List<AggregatedFeatureEventConf> featureEventConfList = aggrFeatureEventsConfService.getAggregatedFeatureEventConfList(featureBucketAggrSendingQueue.getFeatureBucketConfName());
			for(AggregatedFeatureEventConf conf: featureEventConfList){
				if(bucket == null){
					bucket = featureBucketsService.getFeatureBucket(conf.getBucketConf(), featureBucketAggrSendingQueue.getBucketId());
					if(bucket == null){
						String message = String.format("Couldn't send the aggregation event since the bucket was not found. conf name: %s bucketId: %s", featureBucketAggrSendingQueue.getFeatureBucketConfName(), featureBucketAggrSendingQueue.getBucketId());
						logger.error(message);
						throw new RuntimeException(message);
					}
					bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
				}
				// Calculating the new feature
				Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);

				// Building the event
				JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());

				// Sending the event
				sendEvent(event);
			}

    	}
    	featureBucketAggrSendingQueueRepository.deleteByFireTimeLessThan(fireTime);

    }



    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }
}
