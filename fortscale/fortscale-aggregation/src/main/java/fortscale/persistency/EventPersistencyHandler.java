package fortscale.persistency;

import net.minidev.json.JSONObject;

import java.io.IOException;

public interface EventPersistencyHandler {
	void saveEvent(JSONObject event) throws IOException;
}
