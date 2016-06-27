package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.streaming.service.aggregation.feature.event.metrics.AggrInternalAndKafkaEventTopologyServiceMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class AggrInternalAndKafkaEventTopologyService extends AggrKafkaEventTopologyService {
	private static final Logger logger = Logger.getLogger(AggrInternalAndKafkaEventTopologyService.class);

	@Autowired
	private StatsService statsService;

	private AggrInternalAndKafkaEventTopologyServiceMetrics metrics;
	private AggregatorManager aggregatorManager;

	public AggrInternalAndKafkaEventTopologyService() {
		metrics = new AggrInternalAndKafkaEventTopologyServiceMetrics(statsService);
	}
	
	@Override
	public boolean sendEvent(JSONObject event) {
		boolean isSucceed = super.sendEvent(event);
		if(isSucceed){
			try {
				aggregatorManager.processEvent(event);
			} catch (Exception e) {
				logger.error("Failed to process aggregated event", e);
				metrics.failed++;
				isSucceed = false;
			}
		}
		
		return isSucceed;
	}

	public void setAggregatorManager(AggregatorManager aggregatorManager) {
		this.aggregatorManager = aggregatorManager;
	}
	
	
}
