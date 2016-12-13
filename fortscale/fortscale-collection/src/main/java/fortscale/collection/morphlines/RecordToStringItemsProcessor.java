package fortscale.collection.morphlines;

import fortscale.collection.metrics.RecordToStringItemsProcessorMetric;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;


/**
 * Converts morphline record into string line
 */
public class RecordToStringItemsProcessor {

	private static Logger logger = Logger.getLogger(RecordToStringItemsProcessor.class);

	private String[] fields;
	private String separator;

	private RecordToStringItemsProcessorMetric metric;

	final static Map<String, RecordToStringItemsProcessorMetric> metricsMap = new HashMap<>();

	public RecordToStringItemsProcessor(String separator, StatsService statsService, String name, String... fields) throws IllegalArgumentException {
		Assert.notNull(separator);
		Assert.notNull(fields);
		Assert.notEmpty(fields);
		initMetricsClass(statsService, name);
		this.fields = fields;
		this.separator = separator;
	}

	public String process(Record record) {
		metric.record++;
		if (record == null) {
			metric.recordFailedBecauseEmpty++;
			return null;
		}

		boolean firstItem = true;
		boolean noValues = true;
		StringBuilder sb = new StringBuilder();
		for (String field : fields) {
			if (!firstItem) {
				sb.append(separator);
			}

			Object value = record.getFirstValue(field);
			if (value != null) {
				sb.append(value.toString().trim());
				noValues = false;
			}

			firstItem = false;
		}

		if (noValues) {
			metric.recordFailedBecauseNoValues++;
			return null;
		} else {
			return sb.toString();
		}
	}

	public String toJSON(Record record) {
		metric.record++;
		if (record == null) {
			metric.recordFailedBecauseNoValues++;
			return null;
		}

		JSONObject json = new JSONObject();
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			if (value != null) {
				json.put(field, value);
			}
		}
		return json.toJSONString(JSONStyle.NO_COMPRESS);
	}

	static synchronized void x() {}

	public void initMetricsClass(StatsService statsService, String name) {

		synchronized (metricsMap) {

			// Check if we already have the metrics for this name. If so, reuse it
			RecordToStringItemsProcessorMetric tmpMetric = metricsMap.get(name);

			if (tmpMetric != null) {
				// Yes, use the existing metric
				metric = tmpMetric;

				logger.debug("initMetricsClass() - reusing metric {} for {}", metric, name);
				return;
			}

			// No, create a new metric, save it and use it
			metric = new RecordToStringItemsProcessorMetric(statsService, name);
			logger.debug("initMetricsClass() - created metric {} for {}", metric, name);

			metricsMap.put(name, metric);

		}

	}

}
