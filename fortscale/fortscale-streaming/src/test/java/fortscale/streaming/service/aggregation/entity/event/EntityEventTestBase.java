package fortscale.streaming.service.aggregation.entity.event;

import org.springframework.beans.factory.annotation.Value;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilder;
import net.minidev.json.JSONObject;

public class EntityEventTestBase {

	@Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
	

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

		JSONObject message = new JSONObject();
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_FEATURE_TYPE, aggrFeatureType);
		message.put(bucketConfNameFieldName, bucketConfName);
		message.put(aggrFeatureNameFieldName, aggrFeatureName);
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_AGGREGATED_FEATURE_VALUE, aggrFeatureValue);
		message.put("score", score);
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_DATE_TIME_UNIX, dateTime);
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_START_TIME_UNIX, startTime);
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_END_TIME_UNIX, endTime);
		message.put(AggrFeatureEventBuilder.EVENT_FIELD_CONTEXT, context);
		return message;
	}
}
