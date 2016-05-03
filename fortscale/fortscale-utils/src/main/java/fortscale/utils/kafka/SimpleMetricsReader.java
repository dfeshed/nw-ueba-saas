package fortscale.utils.kafka;

import fortscale.utils.ConversionUtils;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleMetricsReader extends AbstractKafkaTopicReader {
	private static final String TOPIC = "metrics";
	private static final String HEADER = "header";
	private static final String JOB_NAME = "job-name";
	private static final String METRICS = "metrics";

	private String jobName;
	private String className;
	private Collection<String> metricsToCapture;
	private volatile Map<String, Object> capturedMetrics;

	public SimpleMetricsReader(
			String clientId, int partition,
			String jobName, String className,
			Collection<String> metricsToCapture) {

		super(clientId, TOPIC, partition);

		Assert.hasText(jobName);
		Assert.hasText(className);
		Assert.notEmpty(metricsToCapture);
		metricsToCapture.forEach(Assert::hasText);

		this.jobName = jobName;
		this.className = className;
		this.metricsToCapture = metricsToCapture;
		this.capturedMetrics = new HashMap<>();
	}

	public Long getLong(String metricName) {
		return ConversionUtils.convertToLong(capturedMetrics.get(metricName));
	}

	@Override
	protected void handleMessage(JSONObject message) {
		JSONObject classMetrics = getClassMetrics(message);

		if (classMetrics != null) {
			for (String metricToCapture : metricsToCapture) {
				Object capturedMetric = classMetrics.get(metricToCapture);

				if (capturedMetric != null) {
					capturedMetrics.put(metricToCapture, capturedMetric);
				}
			}
		}
	}

	private JSONObject getClassMetrics(JSONObject message) {
		JSONObject classMetrics = null;

		try {
			if (message.getJSONObject(HEADER).getString(JOB_NAME).equals(jobName)) {
				classMetrics = message.getJSONObject(METRICS).getJSONObject(className);
			}
		} catch (Exception e) {
			classMetrics = null;
		}

		return classMetrics;
	}
}
