package fortscale.streaming.metrics;

import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Gauge;
import org.apache.samza.metrics.MetricsVisitor;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;

public class MongoMetricsVisitor extends MetricsVisitor {

	private JobProgressReporter reporter;
	private String monitorId;
	
	public MongoMetricsVisitor(JobProgressReporter reporter, String monitorId) {
		this.reporter = reporter;
		this.monitorId = monitorId;
	}
	
	@Override
	public void counter(Counter counter) {
		reporter.addDataReceived(monitorId, new JobDataReceived(counter.getName(), (int)counter.getCount(), ""));		
	}

	@Override
	public <T> void gauge(Gauge<T> gauge) {
		// ignore as we do not support reporting data currently in the mongo monitor for gauge
	}

}
