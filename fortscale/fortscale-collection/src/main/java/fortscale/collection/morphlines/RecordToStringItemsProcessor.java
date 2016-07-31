package fortscale.collection.morphlines;

import fortscale.collection.metrics.RecordToStringItemsProcessorMetric;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;


/**
 * Converts morphline record into string line
 */
public class RecordToStringItemsProcessor {

	private String[] fields;
	private String separator;

	private RecordToStringItemsProcessorMetric metric;
	
	public RecordToStringItemsProcessor(String separator, StatsService statsService, String name, String... fields) throws IllegalArgumentException {
		Assert.notNull(separator);
		Assert.notNull(fields);
		Assert.notEmpty(fields);
		initMetricsClass(statsService,name);
		this.fields = fields;
		this.separator = separator;
	}
	
	public String process(Record record) {
		metric.record++;
		if (record==null) {
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
			if (value!=null) {
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
		if (record==null) {
			metric.recordFailedBecauseNoValues++;
			return null;
		}
			
		JSONObject json = new JSONObject();
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			if (value!=null) {
				json.put(field, value);				
			}
		}
		return json.toJSONString(JSONStyle.NO_COMPRESS);
	}

	public void initMetricsClass(StatsService statsService, String name){
		metric = new RecordToStringItemsProcessorMetric(statsService,name);
	}
}
