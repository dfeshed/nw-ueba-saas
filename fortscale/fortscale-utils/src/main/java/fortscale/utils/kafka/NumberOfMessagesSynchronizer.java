package fortscale.utils.kafka;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

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
	private int timeToWaitInMilliseconds;
	private int retries;

	public NumberOfMessagesSynchronizer(String jobClassToMonitor, String jobToMonitor, int timeToWaitInMilliseconds,
			int retries) {
		List<String> metrics = new ArrayList();
		metrics.add(String.format("%s-received-message-count", jobToMonitor));
		this.jobClassToMonitor = jobClassToMonitor;
		this.jobToMonitor = jobToMonitor;
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
		this.retries = retries;

		long initializeMetricsSize = MetricsReader.getCounterMetricsSum(metrics, zookeeperConnection.split(":")[0],
				Integer.parseInt(zookeeperConnection.split(":")[1]), jobClassToMonitor, jobToMonitor);
		this.decider = new ReachSumMetricsDecider(metrics, initializeMetricsSize);
	}

	@Override public boolean synchronize(long numberOfSentEvents) {
		decider.updateParams(numberOfSentEvents);
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