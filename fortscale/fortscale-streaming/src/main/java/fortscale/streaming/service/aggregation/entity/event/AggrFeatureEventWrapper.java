package fortscale.streaming.service.aggregation.entity.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilder;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;

@Configurable(preConstruction = true)
public class AggrFeatureEventWrapper {
	private static final String BLANK_STRING = "";

	private static final String AGGREGATED_FEATURE_TYPE_F_VALUE = "F";
	private static final String AGGREGATED_FEATURE_TYPE_P_VALUE = "P";
	private static final String SCORE_FIELD = "score";
	
	@Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
	@Value("${streaming.aggr_event.field.context}")
	private String contextFieldName;

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
		return ConversionUtils.convertToString(aggrFeatureEvent.get(AggrFeatureEventBuilder.EVENT_FIELD_FEATURE_TYPE));
	}

	public boolean isOfTypeF() {
		return AGGREGATED_FEATURE_TYPE_F_VALUE.equals(getAggregatedFeatureType());
	}

	public boolean isOfTypeP() {
		return AGGREGATED_FEATURE_TYPE_P_VALUE.equals(getAggregatedFeatureType());
	}

	public String getBucketConfName() {
		return ConversionUtils.convertToString(aggrFeatureEvent.get(bucketConfNameFieldName));
	}

	public String getAggregatedFeatureName() {
		return ConversionUtils.convertToString(aggrFeatureEvent.get(aggrFeatureNameFieldName));
	}

	public Double getAggregatedFeatureValue() {
		return ConversionUtils.convertToDouble(aggrFeatureEvent.get(aggrFeatureValueFieldName));
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
		return ConversionUtils.convertToLong(aggrFeatureEvent.get(AggrFeatureEventBuilder.EVENT_FIELD_START_TIME_UNIX));
	}

	public Long getEndTime() {
		return ConversionUtils.convertToLong(aggrFeatureEvent.get(AggrFeatureEventBuilder.EVENT_FIELD_END_TIME_UNIX));
	}

	private void createContextMap() {
		JSONObject context;
		this.context = new HashMap<>();

		try {
			context = (JSONObject)aggrFeatureEvent.get(contextFieldName);
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
