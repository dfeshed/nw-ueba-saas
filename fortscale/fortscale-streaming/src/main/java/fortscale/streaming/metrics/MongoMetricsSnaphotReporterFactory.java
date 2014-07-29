package fortscale.streaming.metrics;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsReporter;
import org.apache.samza.metrics.MetricsReporterFactory;

/**
 * Constructs a MongoMetricsSnaphotReporter to report job metrics to mongodb periodically
 */
public class MongoMetricsSnaphotReporterFactory implements MetricsReporterFactory {

	@Override
	public MetricsReporter getMetricsReporter(String name, String containerName, Config config) {
		// get job name from configuration
		String jobName = config.get("job.name");
		
		return new MongoMetricsSnapshotReporter(jobName);
	}

}
