package fortscale.aggregation.feature.event;

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
public class AggrFeatureEventBdpModeService extends AggrFeatureEventImprovedService{

	@Value("${fetch.data.cycle.in.seconds}")
	private long fetchDataCycleInSeconds;

	public AggrFeatureEventBdpModeService(AggregatedFeatureEventsConfService aggrFeatureEventsConfService, FeatureBucketsService featureBucketsService) {
		super(aggrFeatureEventsConfService, featureBucketsService);
	}

	@Override
    public void sendEvents(long curEventTime){
    	//moving feature bucket to sending queue
    	long curTime = System.currentTimeMillis()/1000;
    	long endTime = curEventTime+fetchDataCycleInSeconds;
    	featureBucketAggrMetadataRepository.deleteByEndTimeLessThan(endTime);
    }
}
