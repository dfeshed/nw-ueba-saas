package presidio.webapp.controllers.alerts;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.webapp.model.*;
import presidio.webapp.service.RestAlertService;

import java.util.List;

@Controller
public class AlertsController implements AlertsApi {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @Override
    public ResponseEntity<Alert> getAlert(String alertId) {
        if (!StringUtils.isEmpty(alertId)) {
            Alert alert = restAlertService.getAlertById(alertId);
            if (alert != null) {
                return new ResponseEntity(alert, HttpStatus.OK);
            }
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlerts(AlertQuery alertQuery) {
        List<Alert> alerts = restAlertService.getAlerts(alertQuery);
        if (alerts != null) {
            AlertsWrapper alertsWrapper = new AlertsWrapper();
            alertsWrapper.setAlerts(alerts);
            alertsWrapper.setTotal(alerts.size());
            alertsWrapper.setPage(0);
            return new ResponseEntity(alertsWrapper, HttpStatus.OK);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<List<InlineResponse2001>> getIndicatorByAlert(Integer indicatorId, Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<List<InlineResponse200>> getIndicatorEventsByAlert(Integer indicatorId, Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<List<InlineResponse200>> getIndicatorsByAlert(Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<Alert> updateAlert(List<Patch> patch) {
        return null;
    }
}
