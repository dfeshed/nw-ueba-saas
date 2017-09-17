package presidio.webapp.controllers.alerts;

import fortscale.utils.logging.Logger;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertQuery;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.InlineResponse200;
import presidio.webapp.model.InlineResponse2001;
import presidio.webapp.model.Patch;
import presidio.webapp.service.RestAlertService;

import java.util.List;

@Controller
public class AlertsController implements AlertsApi {

    private final Logger logger = Logger.getLogger(AlertsController.class);

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @Override
    public ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return", required = true) @PathVariable("alertId") String alertId) {
        try {
            Alert alert = restAlertService.getAlertById(alertId);
            HttpStatus httpStatus = alert != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return new ResponseEntity(alert, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get alert by alertId:{} , But got internal error {}",alertId,ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlerts(AlertQuery alertQuery) {
        try {
            AlertsWrapper alertsWrapper=restAlertService.getAlerts(alertQuery);
            HttpStatus httpStatus = alertsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return new ResponseEntity(alertsWrapper, httpStatus);
        }
        catch (Exception ex){
            logger.error("Trying the to get alerts with this alertQuery:{} , But got internal error {}",alertQuery.toString(),ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
