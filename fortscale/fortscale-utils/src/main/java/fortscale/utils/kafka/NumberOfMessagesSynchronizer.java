package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 31/12/2015.
 */
@Configurable(preConstruction = true)
public class NumberOfMessagesSynchronizer implements IKafkaSynchronizer {

	private static Logger logger = Logger.getLogger(NumberOfMessagesSynchronizer.class);

	@Value("${broker.list}") protected String zookeeperConnection;

	private ReachSumMetricsDecider decider;
	private String jobClassToMonitor;
	private String jobToMonitor;
	int timeToWaitInMilliseconds;
	int retries;

	public NumberOfMessagesSynchronizer() {
		// Default constructor
	}

	public NumberOfMessagesSynchronizer(String jobClassToMonitor, String jobToMonitor,
			int timeToWaitInMilliseconds, int retries, int batchSize) {
		List<String> metrics = new ArrayList<String>();
		metrics.add(jobToMonitor);
		this.decider = new ReachSumMetricsDecider(metrics, batchSize);
		this.jobClassToMonitor = jobClassToMonitor;
		this.jobToMonitor = jobToMonitor;
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
		this.retries = retries;
	}

	@Override public boolean synchronize(long latestEpochTimeSent) {
		boolean result = MetricsReader.waitForMetrics(zookeeperConnection.split(":")[0],
				Integer.parseInt(zookeeperConnection.split(":")[1]), jobClassToMonitor, jobToMonitor,
				decider, timeToWaitInMilliseconds, retries);
		if (result == false) {
			logger.error("last message not processed - timed out!");
			return false;
		}
		logger.info("last message in batch processed, moving to next batch");
		return true;
	}
}
