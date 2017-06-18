package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomerd on 31/12/2015.
 */
@Configurable(preConstruction = true)
public class MetricsKafkaSynchronizer implements IKafkaSynchronizer {

	private static Logger logger = Logger.getLogger(MetricsKafkaSynchronizer.class);

	@Value("${broker.list}") protected String zookeeperConnection;

	private EqualityMetricsDecider decider;
	private String jobClassToMonitor;
	private String jobToMonitor;
	int timeToWaitInMilliseconds;
	int retries;

	public MetricsKafkaSynchronizer() {
		// Default constructor
	}

	public MetricsKafkaSynchronizer(String jobClassToMonitor, String jobToMonitor,
			int timeToWaitInMilliseconds, int retries) {
		this.decider = new EqualityMetricsDecider();
		this.jobClassToMonitor = jobClassToMonitor;
		this.jobToMonitor = jobToMonitor;
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
		this.retries = retries;
	}

	@Override public boolean synchronize(long latestEpochTimeSent) {
		Map<String, Object> keyToExpectedValueMap = new HashMap<>();
		keyToExpectedValueMap.put(String.format("%s-last-message-epochtime", jobToMonitor), latestEpochTimeSent);
		decider.updateParams(keyToExpectedValueMap);
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
