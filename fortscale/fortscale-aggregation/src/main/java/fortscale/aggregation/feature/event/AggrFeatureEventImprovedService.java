package fortscale.aggregation.feature.event;

import fortscale.aggregation.domain.feature.event.FeatureBucketAggrMetadata;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrMetadataRepository;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrSendingQueue;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrSendingQueueRepository;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.event.metrics.AggrFeatureEventBucketConfMetrics;
import fortscale.aggregation.feature.event.metrics.AggrFeatureEventMetrics;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.common.feature.Feature;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private StatsService statsService;

    private FeatureBucketsService featureBucketsService;

    private Map<String, List<AggregatedFeatureEventConf>> bucketConfName2FeatureEventConfMap = new HashMap<>();

	private AggrFeatureEventMetrics metrics;
	private Map<String, AggrFeatureEventBucketConfMetrics> bucketConfNameToMetrics = new HashMap<>();

    public AggrFeatureEventImprovedService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketsService featureBucketsService) {
		metrics = new AggrFeatureEventMetrics(statsService);
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
				bucketConfNameToMetrics.put(bucketConfName, new AggrFeatureEventBucketConfMetrics(statsService, bucketConfName));
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
			String featureBucketConfName = featureBucket.getFeatureBucketConfName();
			if(bucketConfName2FeatureEventConfMap.get(featureBucketConfName) != null){
	        	FeatureBucketAggrMetadata featureBucketAggrMetadata = new FeatureBucketAggrMetadata(featureBucket);
	        	featureBucketAggrMetadataList.add(featureBucketAggrMetadata);
				bucketConfNameToMetrics.get(featureBucketConfName).featureBucketAggrMetadaSaves++;
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
    	long endTime = curEventTime+fetchDataCycleInSeconds;
    	for(FeatureBucketAggrMetadata aggrMetadata: featureBucketAggrMetadataRepository.findByEndTimeLessThan(endTime)){
			String featureBucketConfName = aggrMetadata.getFeatureBucketConfName();
			FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue = new FeatureBucketAggrSendingQueue(featureBucketConfName, aggrMetadata.getBucketId(), curTime, aggrMetadata.getEndTime());
    		featureBucketAggrSendingQueueRepository.save(featureBucketAggrSendingQueue);
			bucketConfNameToMetrics.get(featureBucketConfName).sentToQueue++;
    	}
		featureBucketAggrMetadataRepository.deleteByEndTimeLessThan(endTime);

    	//creating and sending feature aggregated events
    	List<FeatureBucketAggrSendingQueue> featureBucketAggrSendingQueueList = null;
    	long fireTime = curTime-waitingTimeBeforeNotification;
		metrics.fireTimeEpoch = fireTime;
    	if(useEndTimeSort){
    		featureBucketAggrSendingQueueList = featureBucketAggrSendingQueueRepository.findByFireTimeLessThan(fireTime, new Sort(Direction.ASC, FeatureBucketAggrSendingQueue.END_TIME_FIELD));
		} else{
			featureBucketAggrSendingQueueList = featureBucketAggrSendingQueueRepository.findByFireTimeLessThan(fireTime);
		}
    	for(FeatureBucketAggrSendingQueue featureBucketAggrSendingQueue: featureBucketAggrSendingQueueList){
    		FeatureBucket bucket = null;
    		List<Map<String, Feature>> bucketAggrFeaturesMapList = new ArrayList<>();

			String featureBucketConfName = featureBucketAggrSendingQueue.getFeatureBucketConfName();
			List<AggregatedFeatureEventConf> featureEventConfList = bucketConfName2FeatureEventConfMap.get(featureBucketConfName);
    		if(featureEventConfList != null){
	    		for(AggregatedFeatureEventConf conf: featureEventConfList){
					AggrFeatureEventBucketConfMetrics aggrFeatureEventMetrics = bucketConfNameToMetrics.get(featureBucketConfName);
	    			if(bucket == null){
	    				bucket = featureBucketsService.getFeatureBucket(conf.getBucketConf(), featureBucketAggrSendingQueue.getBucketId());
	    				if(bucket == null){
	    					String message = String.format("Couldn't send the aggregation event since the bucket was not found. conf name: %s bucketId: %s", featureBucketConfName, featureBucketAggrSendingQueue.getBucketId());
	    					logger.error(message);
							aggrFeatureEventMetrics.bucketsNotFound++;
	    					throw new RuntimeException(message);
	    				}
	    				bucketAggrFeaturesMapList.add(bucket.getAggregatedFeatures());
	    			}
	    			// Calculating the new feature
	                Feature feature = aggrFeatureFuncService.calculateAggrFeature(conf, bucketAggrFeaturesMapList);
					if (conf.getType().equals("F")) {
						aggrFeatureEventMetrics.Fs++;
					} else {
						aggrFeatureEventMetrics.Ps++;
					}

	                // Building the event
	                JSONObject event = aggrFeatureEventBuilderService.buildEvent(conf, bucket.getContextFieldNameToValueMap(), feature, bucket.getStartTime(), bucket.getEndTime());

	                // Sending the event
	                sendEvent(event);
	    		}
    		} else {
				metrics.invalidFeatureBucketConfNames++;
			}
    	}
    	featureBucketAggrSendingQueueRepository.deleteByFireTimeLessThan(fireTime);

    }



    private void sendEvent(JSONObject event) {
        aggrEventTopologyService.sendEvent(event);
    }
}
