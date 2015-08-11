package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

public class AggrInternalAndKafkaEventTopologyService extends AggrKafkaEventTopologyService {
	private static final Logger logger = Logger.getLogger(AggrInternalAndKafkaEventTopologyService.class);
	
	
	private AggregatorManager aggregatorManager;
	
	@Override
	public boolean sendEvent(JSONObject event) {
		boolean isSucceed = super.sendEvent(event);
		if(isSucceed){
			try {
				aggregatorManager.processEvent(event);
			} catch (Exception e) {
				logger.error("Failed to process aggregated event", e);
				isSucceed = false;
			}
		}
		
		return isSucceed;
	}

	public void setAggregatorManager(AggregatorManager aggregatorManager) {
		this.aggregatorManager = aggregatorManager;
	}
	
	
}
