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
	@Value("${fortscale.samza.aggregation.prevalence.stats.metrics.class}")
	private String metricsClassName;

	private TaskContext context;
	private Map<String, Counter> counters;

	public AggregatedFeatureAndEntityEventsMetricsService(TaskContext context) {
		Assert.notNull(context);
		this.context = context;
		this.counters = new HashMap<>();
	}

	public void updateMetrics(String dataSource) {
		if (StringUtils.hasText(dataSource)) {
			incCounter(dataSource);
		}
	}

	private void incCounter(String dataSource) {
		getCounter(dataSource).inc();
	}

	private Counter getCounter(String dataSource) {
		Counter counter = counters.get(dataSource);

		if (counter == null) {
			String counterName = String.format("%s%s", dataSource, dataSourceCounterNameSuffix);
			counter = context.getMetricsRegistry().newCounter(metricsClassName, counterName);
			counters.put(dataSource, counter);
		}

		return counter;
	}
}
