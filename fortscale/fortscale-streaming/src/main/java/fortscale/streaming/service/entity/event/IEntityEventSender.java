package fortscale.streaming.service.entity.event;

import net.minidev.json.JSONObject;

public interface IEntityEventSender {
	/**
	 * @param entityEvent The entity event to send.
	 *                    If entityEvent == null, the entity event will be discarded.
	 */
	public void send(JSONObject entityEvent);
}
