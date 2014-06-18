package fortscale.streaming.task;

import net.minidev.json.JSONObject;

public class VPNEventsModelStreamTask extends EventsPrevalenceModelStreamTask {

	private static final String CLOSED = "CLOSED";
	private static final String STATUS_FIELD = "status";
	
	@Override
	protected boolean acceptMessage(JSONObject message) {
		// filter out vpn events with closed status
		return !CLOSED.equals(message.get(STATUS_FIELD));
	}
	
}
