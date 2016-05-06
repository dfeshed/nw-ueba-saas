package fortscale.entity.event;

import net.minidev.json.JSONObject;

import java.util.concurrent.TimeoutException;

public interface IEntityEventSender {
	/**
	 * @param entityEvent The entity event to send.
	 *                    If entityEvent == null, the entity event will be discarded.
	 */
	void send(JSONObject entityEvent) throws TimeoutException;

	default void throttle() throws TimeoutException {}
}
