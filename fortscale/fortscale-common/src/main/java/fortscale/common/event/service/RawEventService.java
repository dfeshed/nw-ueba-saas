package fortscale.common.event.service;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.Event;
import fortscale.common.event.RawEvent;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class RawEventService implements EventService {
	@Value("${streaming.event.datasource.field.name}")
	private String dataSourceFieldName;

	@Autowired
	private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;

	@Override
	public Event getEvent(JSONObject message) {
		if (message == null) {
			throw new IllegalStateException("Input message cannot be null.");
		}

		String dataSource = message.getAsString(dataSourceFieldName);

		if (dataSource == null) {
			throw new IllegalStateException(String.format(
					"Message must contain a %s field: %s.",
					dataSourceFieldName, message.toJSONString()));
		}

		return new RawEvent(message, dataEntitiesConfigWithBlackList, dataSource);
	}
}
