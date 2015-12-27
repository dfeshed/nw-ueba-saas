package fortscale.streaming.metrics;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsReporter;
import org.apache.samza.metrics.MetricsReporterFactory;

import fortscale.streaming.ConfigUtils;
import fortscale.services.impl.SpringService;

/**
 * Constructs a MongoMetricsSnaphotReporter to report job metrics to mongodb periodically
 */
public class MongoMetricsSnapshotReporterFactory implements MetricsReporterFactory {

	@Override
	public MetricsReporter getMetricsReporter(String name, String containerName, Config config) {
		// get job name from configuration
		String jobName = config.get("job.name");
		
		// prepare the SpringService according to context in config
		String contextPath = ConfigUtils.getConfigString(config, "fortscale.context");
		SpringService.init(contextPath);
		
		return new MongoMetricsSnapshotReporter(jobName);
	}

}
