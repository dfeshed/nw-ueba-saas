package fortscale.streaming.service;

import org.apache.samza.metrics.Counter;
import org.apache.samza.task.TaskContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AggregatedFeatureAndEntityEventsMetricsService {
	private static final String DATA_SOURCE_COUNTER_NAME_FORMAT = "%s-counter";

	private TaskContext context;
	private Map<String, Counter> counters;

	public AggregatedFeatureAndEntityEventsMetricsService(TaskContext context) {
		Assert.notNull(context);
		this.context = context;
		this.counters = new HashMap<>();
	}

	public void updateMetrics(String dataSource) {
		if (StringUtils.hasText(dataSource)) {
			String counterName = String.format(DATA_SOURCE_COUNTER_NAME_FORMAT, dataSource);
			incCounter(counterName);
		}
	}

	private void incCounter(String counterName) {
		Counter counter = getCounter(counterName);
		counter.inc();
	}

	private Counter getCounter(String counterName) {
		Counter counter = counters.get(counterName);

		if (counter == null) {
			counter = context.getMetricsRegistry().newCounter(getClass().getName(), counterName);
			counters.put(counterName, counter);
		}

		return counter;
	}
}
