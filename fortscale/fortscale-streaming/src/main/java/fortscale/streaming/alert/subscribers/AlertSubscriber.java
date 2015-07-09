package fortscale.streaming.alert.subscribers;

import com.espertech.esper.client.EPServiceProvider;
import fortscale.services.AlertsService;

/**
 * Created by rans on 09/07/15.
 * an interface for the Alert subscribers
 */
public interface AlertSubscriber {
    /**
     * an init function that initializes the Alert Subscriber
     * @param alertsService the MongoDB service to write alerts
     * @param ruleNam the rule name that will be written with the alert
     */
    public void init(AlertsService alertsService, String ruleNam);
}
