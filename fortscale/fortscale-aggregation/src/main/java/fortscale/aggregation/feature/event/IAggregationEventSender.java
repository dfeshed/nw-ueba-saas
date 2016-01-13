package fortscale.aggregation.feature.event;

import net.minidev.json.JSONObject;

/**
 * Created by YaronDL on 12/31/2015.
 */
public interface IAggregationEventSender {
    public void send(boolean isOfTypeF, JSONObject event);
    public void callSynchronizer(long epochTime);
}
