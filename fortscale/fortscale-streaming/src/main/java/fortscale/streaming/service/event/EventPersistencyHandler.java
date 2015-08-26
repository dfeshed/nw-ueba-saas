package fortscale.streaming.service.event;

import net.minidev.json.JSONObject;

/**
 * Created by amira on 23/08/2015.
 */
public interface EventPersistencyHandler {
    void saveEvent(JSONObject event, String collectionPrefix);
}
