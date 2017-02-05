package fortscale.domain.core.dao.rest;

import fortscale.domain.core.alert.Alert;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for alerts request in the REST API
 */
public class Alerts implements Serializable {
    private static final long serialVersionUID = 9109074252875664042L;
    private List<Alert> alerts;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

}
