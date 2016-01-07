package fortscale.entity.event;

import net.minidev.json.JSONObject;

public interface IEntityEventSender {
	/**
	 * @param entityEvent The entity event to send.
	 *                    If entityEvent == null, the entity event will be discarded.
	 */
	void send(JSONObject entityEvent);
}
