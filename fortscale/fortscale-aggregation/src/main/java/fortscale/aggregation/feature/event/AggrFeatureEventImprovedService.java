package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
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

    private Map<String, List<AggregatedFeatureEventConf>> bucketConfName2FeatureEventConfMap = new HashMap<>();

    public AggrFeatureEventImprovedService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketsService featureBucketsService) {
        this.featureBucketsService = featureBucketsService;
        Assert.notNull(aggrFeatureEventsConfService);
        Assert.notNull(featureBucketsService);
        List<AggregatedFeatureEventConf> aggrFeatureEventConfs = aggrFeatureEventsConfService.getAggregatedFeatureEventConfList();
        if(aggrFeatureEventConfs==null || aggrFeatureEventConfs.isEmpty()) {
            logger.info(INFO_MSG_NO_EVENT_CONFS);
        }
        
        for(AggregatedFeatureEventConf eventConf : aggrFeatureEventConfs) {
        	String bucketConfName = eventConf.getBucketConfName();
        	List<AggregatedFeatureEventConf> bucketAggFeatureEventConfList = bucketConfName2FeatureEventConfMap.get(bucketConfName);
        	if(bucketAggFeatureEventConfList == null){
        		bucketAggFeatureEventConfList = new ArrayList<>();
        		bucketConfName2FeatureEventConfMap.put(bucketConfName, bucketAggFeatureEventConfList);
        	}
        	
        	bucketAggFeatureEventConfList.add(eventConf);
        }
    }

    /**
     * Handling new feature buckets.
     * The assumption is that new buckets are coming in order.
     */
    public void newFeatureBuckets(List<FeatureBucket> buckets) {
    	List<FeatureBucketAggrMetadata> featureBucketAggrMetadataList = new ArrayList<>();
        for(FeatureBucket featureBucket : buckets) {
        	if(bucketConfName2FeatureEventConfMap.get(featureBucket.getFeatureBucketConfName()) != null){
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
        	if(bucketConfName2FeatureEventConfMap.get(featureBucket.getFeatureBucketConfName()) != null){
        		featureBucketAggrMetadataRepository.updateFeatureBucketsEndTime(featureBucket.getFeatureBucketConfName(), featureBucket.getBucketId(), featureBucket.getEndTime());
        	}
        }
    }
    
    public void sendEvents(long curEventTime){
    	//moving feature bucket to sending queue
    	long curTime = System.currentTimeMillis()/1000;
    	for(FeatureBucketAggrMetadata aggrMetadata: featureBucketAggrMetadataRepository.findByEndTimeLessThan(curEventTime+fetchDataCycleInSeconds)){
    		FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue = new FeatureBucketAggrSendingQueue(aggrMetadata.getFeatureBucketConfName(), aggrMetadata.getBucketId(), curTime);
    		featureBucketAggrSendingQueueRepository.save(featureBucketAggrSendingQueue);
    		featureBucketAggrMetadataRepository.delete(aggrMetadata);
    	}
    	
    	//creating and sending feature aggregated events
    	for(FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue: featureBucketAggrSendingQueueRepository.findByFireTimeLessThan(curTime-waitingTimeBeforeNotification)){
    		FeatureBucket bucket = null;
    		List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();
    		
    		List<AggregatedFeatureEventConf> featureEventConfList = bucketConfName2FeatureEventConfMap.get(featureBucketAggrSendingQueue.getFeatureBucketConfName());
    		if(featureEventConfList != null){
	    		for(AggregatedFeatureEventConf conf: featureEventConfList){
	    			if(bucket == null){
	    				bucket = featureBucketsService.getFeatureBucket(conf.getBucketConf(), featureBucketAggrSendingQueue.getBucketId());
	    				bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
	    			}
	    			// Calculating the new feature
	                Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);
	
	                // Building the event
	                JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());
	
	                // Sending the event
	                sendEvent(event);
	                featureBucketAggrSendingQueueRepository.delete(featureBucketAggrSendingQueue);
	    		}
    		}
    	}
    }
    
    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }
}
