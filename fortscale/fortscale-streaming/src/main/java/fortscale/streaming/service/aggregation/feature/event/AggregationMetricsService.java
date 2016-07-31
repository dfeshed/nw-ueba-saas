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

//TODO: delete this class once BDP is abandoned in favor of DPM
@Configurable(preConstruction = true)
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

	public AggregationMetricsService(TaskContext context) {
		this.context = context;
	}

	public void sentEvent(JSONObject event) {
		String aggregatedFeatureType = aggrFeatureEventBuilderService.getAggregatedFeatureType(event);
		incCounter(AGGREGATION_EVENT_TYPE_COUNTER_FORMAT, aggregatedFeatureType);

		String aggregatedFeatureName = aggrFeatureEventBuilderService.getAggregatedFeatureName(event);
		incCounter(AGGREGATION_FEATURE_EVENT_COUNTER_FORMAT, aggregatedFeatureName);
	}

	private Counter getCounter(String format, String typeOrName) {
		Counter counter = counters.get(typeOrName);
		if (counter == null) {
			String counterName = String.format(format, typeOrName);
			counter = context.getMetricsRegistry().newCounter(getClass().getName(), counterName);
			counters.put(typeOrName, counter);
		}

		return counter;
	}

	private void incCounter(String format, String typeOrName) {
		getCounter(format, typeOrName).inc();
	}
}
