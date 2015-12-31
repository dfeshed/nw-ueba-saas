package fortscale.collection.jobs.smart;

import fortscale.utils.kafka.IMetricsDecider;
import org.json.JSONObject;
import org.springframework.util.Assert;

public class CaptorMetricsDecider implements IMetricsDecider {
	private String metricToCapture;
	private Object capturedMetric;

	/**
	 * @param metricToCapture The key of the metric that needs to be captured.
	 *                        This key cannot be null, empty or blank.
	 */
	public CaptorMetricsDecider(String metricToCapture) {
		Assert.hasText(metricToCapture);
		this.metricToCapture = metricToCapture;
		this.capturedMetric = null;
	}

	@Override
	public boolean decide(JSONObject metrics) {
		if (metrics == null) {
			return false;
		}

		if (metrics.has(metricToCapture)) {
			capturedMetric = metrics.get(metricToCapture);
		}

		return capturedMetric != null;
	}

	public Object getCapturedMetric() {
		return capturedMetric;
	}
}
