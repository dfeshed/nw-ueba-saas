package fortscale.aggregation.feature.event;

import net.minidev.json.JSONObject;

/**
 * Created by amira on 22/07/2015.
 */
public interface AggrEventTopologyService {
    public boolean sendEvent(JSONObject event);
    public String getTopicForEventType(String eventType);
}
