package fortscale.streaming.service.aggregation.feature.event;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import net.minidev.json.JSONObject;
import org.apache.samza.metrics.Counter;
import org.apache.samza.task.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction=true)
public class AggregationMetricsService {
	
	private static final String AGGREGATION_EVENT_TYPE_COUNTER_FORMAT = "aggregated-%s-event-sent-count";
	private static final String AGGREGATION_FEATURE_EVENT_COUNTER_FORMAT = "%s-sent-count";
	
	@Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    
    private TaskContext context;
    
    private Map<String, Counter> counters = new HashMap<>();
    
    public AggregationMetricsService(TaskContext context){
    	this.context = context;
    }
	
	public void sentEvent(JSONObject event){
		String aggregatedFeatureType = aggrFeatureEventBuilderService.getAggregatedFeatureType(event);
		String aggregatedEventTypeCounterStr = String.format(AGGREGATION_EVENT_TYPE_COUNTER_FORMAT, aggregatedFeatureType);
		incCounter(aggregatedEventTypeCounterStr);
		
		String aggregatedFeatureName = aggrFeatureEventBuilderService.getAggregatedFeatureName(event);
		String aggregatedFeatureEventCounterStr =  String.format(AGGREGATION_FEATURE_EVENT_COUNTER_FORMAT, aggregatedFeatureName);
		incCounter(aggregatedFeatureEventCounterStr);
	}
	
	private Counter getCounter(String counterName){
		Counter counter = counters.get(counterName);
		if(counter == null){
			counter = context.getMetricsRegistry().newCounter(getClass().getName(), counterName);
			counters.put(counterName, counter);
		}
		
		return counter;
	}
	
	private void incCounter(String counterName){
		Counter counter = getCounter(counterName);
		counter.inc();
	}

}
