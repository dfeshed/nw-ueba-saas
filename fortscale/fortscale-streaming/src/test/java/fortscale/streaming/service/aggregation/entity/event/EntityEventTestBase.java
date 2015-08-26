package fortscale.streaming.service.aggregation.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;
import org.springframework.beans.factory.annotation.Value;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EntityEventTestBase {

	@Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
	@Value("${streaming.aggr_event.field.context}")
	private String contextFieldName;
	

	protected JSONObject createMessage(
			String aggrFeatureType,
			String bucketConfName,
			String aggrFeatureName,
			double aggrFeatureValue,
			double score,
			long dateTime,
			long startTime,
			long endTime,
			JSONObject context) {

		Map<String, String> newContext = new HashMap<>();
		for(String key: context.keySet()) {
			newContext.put(key, (String)context.get(key));
		}
		JSONObject event = AggrEvent.buildEvent(null, aggrFeatureType, aggrFeatureName, aggrFeatureValue, null, bucketConfName, newContext, startTime, endTime, null, dateTime);
		event.put(AggrEvent.EVENT_FIELD_SCORE, score);
		return event; 

	}
}
