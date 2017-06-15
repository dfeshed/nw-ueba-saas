package fortscale.common.event.service;

import fortscale.common.event.Event;
import net.minidev.json.JSONObject;

public interface EventService {
	Event createEvent(JSONObject message);
}
