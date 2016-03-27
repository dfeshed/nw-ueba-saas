package fortscale.common.event.service;

import fortscale.common.event.Event;
import fortscale.common.feature.extraction.AggrEvent;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

public class AggrEventService implements EventService {
	@Value("${streaming.event.datasource.field.name}")
	private String dataSourceFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_name}")
	private String aggrFeatureNameFieldName;
	@Value("${streaming.aggr_event.field.aggregated_feature_value}")
	private String aggrFeatureValueFieldName;
	@Value("${streaming.aggr_event.field.bucket_conf_name}")
	private String bucketConfNameFieldName;
	@Value("${fortscale.event.context.json.prefix}")
	protected String contextJsonPrefix;


	@Override
	public Event createEvent(JSONObject message) {
		Assert.notNull(message, "Input message cannot be null.");
		String dataSource = message.getAsString(dataSourceFieldName);

		if (dataSource == null) {
			throw new IllegalStateException(String.format(
					"Message must contain a %s field: %s.",
					dataSourceFieldName, message.toJSONString()));
		}

		return new AggrEvent(
				message, aggrFeatureNameFieldName, aggrFeatureValueFieldName,
				bucketConfNameFieldName, dataSource, contextJsonPrefix);
	}
}
