package fortscale.streaming.service.aggregation.entity.event;

import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggrFeatureEventWrapper {
	private static final String BLANK_STRING = "";

	private static final String AGGREGATED_FEATURE_TYPE_FIELD = "aggregated_feature_type";
	private static final String AGGREGATED_FEATURE_TYPE_F_VALUE = "F";
	private static final String AGGREGATED_FEATURE_TYPE_P_VALUE = "P";
	private static final String BUCKET_CONF_NAME_FIELD = "bucket_conf_name";
	private static final String AGGREGATED_FEATURE_NAME_FIELD = "aggregated_feature_name";
	private static final String AGGREGATED_FEATURE_VALUE_FIELD = "aggregated_feature_value";
	private static final String SCORE_FIELD = "score";
	private static final String CONTEXT_FIELD = "context";
	private static final String START_TIME_FIELD = "start_time_unix";
	private static final String END_TIME_FIELD = "end_time_unix";

	private JSONObject aggrFeatureEvent;
	private Map<String, String> context;

	public AggrFeatureEventWrapper(JSONObject aggrFeatureEvent) {
		Assert.notNull(aggrFeatureEvent);
		this.aggrFeatureEvent = aggrFeatureEvent;
		createContextMap();
	}

	public JSONObject unwrap() {
		return aggrFeatureEvent;
	}

	public String getAggregatedFeatureType() {
		return ConversionUtils.convertToString(aggrFeatureEvent.get(AGGREGATED_FEATURE_TYPE_FIELD));
	}

	public boolean isOfTypeF() {
		return AGGREGATED_FEATURE_TYPE_F_VALUE.equals(getAggregatedFeatureType());
	}

	public boolean isOfTypeP() {
		return AGGREGATED_FEATURE_TYPE_P_VALUE.equals(getAggregatedFeatureType());
	}

	public String getBucketConfName() {
		return ConversionUtils.convertToString(aggrFeatureEvent.get(BUCKET_CONF_NAME_FIELD));
	}

	public String getAggregatedFeatureName() {
		return ConversionUtils.convertToString(aggrFeatureEvent.get(AGGREGATED_FEATURE_NAME_FIELD));
	}

	public Double getAggregatedFeatureValue() {
		return ConversionUtils.convertToDouble(aggrFeatureEvent.get(AGGREGATED_FEATURE_VALUE_FIELD));
	}

	public Double getScore() {
		return ConversionUtils.convertToDouble(aggrFeatureEvent.get(SCORE_FIELD));
	}

	public Map<String, String> getContext() {
		return context;
	}

	public Map<String, String> getContext(List<String> contextFields) {
		if (contextFields == null) {
			return null;
		}

		Map<String, String> context = new HashMap<>();
		for (String contextField : contextFields) {
			if (this.context.containsKey(contextField)) {
				context.put(contextField, this.context.get(contextField));
			} else {
				// The requested context cannot be fully deduced from the event,
				// because one of the context fields is missing
				return Collections.emptyMap();
			}
		}

		return context;
	}

	public Long getStartTime() {
		return ConversionUtils.convertToLong(aggrFeatureEvent.get(START_TIME_FIELD));
	}

	public Long getEndTime() {
		return ConversionUtils.convertToLong(aggrFeatureEvent.get(END_TIME_FIELD));
	}

	private void createContextMap() {
		JSONObject context;
		this.context = new HashMap<>();

		try {
			context = (JSONObject)aggrFeatureEvent.get(CONTEXT_FIELD);
			Assert.notNull(context);
		} catch (Exception e) {
			return;
		}

		for (Map.Entry<String, Object> entry : context.entrySet()) {
			String key = entry.getKey();
			String value = ConversionUtils.convertToString(entry.getValue());

			if (StringUtils.isNotBlank(key)) {
				if (StringUtils.isNotBlank(value)) {
					this.context.put(key, value);
				} else {
					this.context.put(key, BLANK_STRING);
				}
			}
		}
	}
}
