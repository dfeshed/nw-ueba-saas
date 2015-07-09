package fortscale.streaming.alert.subscribers;

import com.espertech.esper.client.EPServiceProvider;
import fortscale.services.AlertsService;

/**
 * Created by rans on 09/07/15.
 */
public interface AlertSubscriber {
    public void init(AlertsService alertsService, String ruleNam);
}
