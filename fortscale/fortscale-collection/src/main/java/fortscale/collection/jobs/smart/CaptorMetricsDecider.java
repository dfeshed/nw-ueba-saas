package fortscale.collection.jobs.smart;

import fortscale.utils.kafka.IMetricsDecider;
import fortscale.utils.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CaptorMetricsDecider implements IMetricsDecider {
	private static final Logger logger = Logger.getLogger(CaptorMetricsDecider.class);

	private Collection<String> metricsToCapture;
	private Map<String, Object> capturedMetrics;

	/**
	 * @param metricsToCapture The keys of the metrics that need to be captured.
	 *                         This collection cannot be null or empty.
	 *                         The keys cannot be null, empty or blank.
	 */
	public CaptorMetricsDecider(Collection<String> metricsToCapture) {
		Assert.notEmpty(metricsToCapture);
		metricsToCapture.forEach(Assert::hasText);

		this.metricsToCapture = metricsToCapture;
		this.capturedMetrics = new HashMap<>();
	}

	@Override
	public boolean decide(JSONObject metrics) {
		if (metrics == null) {
			return false;
		}

		metricsToCapture.stream().filter(metrics::has)
				.forEach(metricToCapture -> {
					try {
						capturedMetrics.put(
								metricToCapture, metrics.get(metricToCapture));
					} catch (JSONException e) {
						// this should never happened since we have the "has" filter
						logger.error("error capturing metirc={}",metricToCapture,e);
					}
				});

		return capturedMetrics.size() == metricsToCapture.size();
	}

	public Map<String, Object> getCapturedMetricsMap() {
		return capturedMetrics;
	}
}
