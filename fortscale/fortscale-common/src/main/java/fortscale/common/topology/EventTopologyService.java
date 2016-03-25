package fortscale.common.topology;

import net.minidev.json.JSONObject;

import java.util.List;


public interface EventTopologyService {

    /**
     * This method will send the event to the next topic based on the topology configuration,  according to
     * the current task name (which both must be set prior to calling this method), the running mode (e.g. "regular",
     * "bdp" and according the first matching event field value of the fields configured in the topology configuration
     * file foe the specific task running mode.
     * @param event
     * @return true if event was sent, false otherwise.
     */
    void sendEvent(JSONObject event) throws Exception;
    void setSendingJobName(String sendingTaskName);
    String getOutputTopicForEvent(JSONObject event) throws Exception;
}
