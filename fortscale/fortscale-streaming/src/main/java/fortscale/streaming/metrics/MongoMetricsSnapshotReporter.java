package fortscale.streaming.metrics;

import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsReporter;
import org.apache.samza.metrics.ReadableMetricsRegistry;
import org.apache.samza.util.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.monitor.JobProgressReporter;
import fortscale.services.impl.SpringService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Metrics reporter that saves metrics to mongodb monitor collection periodically 
 */
public class MongoMetricsSnapshotReporter implements MetricsReporter, Runnable {

	private static Logger logger = LoggerFactory.getLogger(MongoMetricsSnapshotReporter.class);
	
	private String jobName;
	private ScheduledExecutorService executor;
	private Map<String, ReadableMetricsRegistry> registries;
	private JobProgressReporter monitor;
	
	public MongoMetricsSnapshotReporter(String jobName) {
		this.jobName = jobName;
		registries = new HashMap<String,ReadableMetricsRegistry>();
		executor = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("MONGO-METRIC-SNAPSHOT-REPORTER"));
		logger.info("Created mongo metrics snapshot reporter [jobName={}]", jobName);
	}

	@Override
	public void start() {
		logger.info("starting mongo metrics snapshot reporter");
		executor.scheduleWithFixedDelay(this, 5, 5, TimeUnit.MINUTES);
	}

	@Override
	public void register(String source, ReadableMetricsRegistry registry) {
		logger.info("registering {} with metrics reporter", source);
		registries.put(source, registry);
	}

	@Override
	public void stop() {
		logger.info("Stopping mongo metrics snapshot reporter");
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.warn("interrupt during execution termination", e);
		}
		if (!executor.isTerminated())
			logger.warn("Unable to shutdown reporter timer");
	}

	/**
	 * Flush metrics to mongodb once called 
	 */
	@Override
	public void run() {
		// get job monitor progress reporter
		if (monitor==null) {
			monitor = SpringService.getInstance().resolve(JobProgressReporter.class);
			if (monitor==null) {
				logger.error("cannot get job progress reporter");
				return;
			}
		}
		
		// start reporting job start
		String monitorId = monitor.startJob("Streaming", jobName, 1, true);
		monitor.startStep(monitorId, "Metrics Snaphot", 1);
		
		// go over metrics and report each of them
		MongoMetricsVisitor visitor = new MongoMetricsVisitor(monitor, monitorId);
		for (String source : registries.keySet()) {
			ReadableMetricsRegistry metricRegistry = registries.get(source);
			for (String group : metricRegistry.getGroups()) {
				visitor.setGroup(group);
				for (Metric metric : metricRegistry.getGroup(group).values()) {
					metric.visit(visitor);
				}
			}
		}
		
		// report job finished
		monitor.finishStep(monitorId, "Metrics Snapshot");
		monitor.finishJob(monitorId);
	}
}
