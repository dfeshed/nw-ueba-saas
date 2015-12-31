package fortscale.streaming.service;

import org.apache.samza.metrics.Counter;
import org.apache.samza.task.TaskContext;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction = true)
public class AggregatedFeatureAndEntityEventsMetricsService {
	@Value("${fortscale.aggregation.events.counter.name.suffix}")
	private String dataSourceCounterNameSuffix;

	private TaskContext context;
	private Map<String, Counter> counters;

	public AggregatedFeatureAndEntityEventsMetricsService(TaskContext context) {
		Assert.notNull(context);
		this.context = context;
		this.counters = new HashMap<>();
	}

	public void updateMetrics(String dataSource) {
		if (StringUtils.hasText(dataSource)) {
			String counterName = String.format("%s%s", dataSource, dataSourceCounterNameSuffix);
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
