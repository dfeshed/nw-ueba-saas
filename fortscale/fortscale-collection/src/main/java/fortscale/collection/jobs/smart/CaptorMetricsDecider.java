package fortscale.collection.jobs.smart;

import fortscale.utils.kafka.IMetricsDecider;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CaptorMetricsDecider implements IMetricsDecider {
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
				.forEach(metricToCapture -> capturedMetrics.put(
				metricToCapture, metrics.get(metricToCapture)));

		return capturedMetrics.size() == metricsToCapture.size();
	}

	public Map<String, Object> getCapturedMetricsMap() {
		return capturedMetrics;
	}
}
