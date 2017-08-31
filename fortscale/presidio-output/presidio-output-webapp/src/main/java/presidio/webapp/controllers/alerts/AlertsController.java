package presidio.webapp.controllers.alerts;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.InlineResponse200;
import presidio.webapp.model.InlineResponse2001;
import presidio.webapp.model.Patch;
import presidio.webapp.service.RestAlertService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by maors on 8/31/2017.
 */
public class AlertsController implements AlertsApi {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }


    @Override
    public ResponseEntity<Alert> alertsAlertIdGet(String alertId) {
        ResponseEntity<Alert> responseEntity=null;
        presidio.webapp.dto.Alert alert= restAlertService.getAlertById(alertId);
        if(alert!=null)
            responseEntity= new ResponseEntity<Alert>(setAlert(alert),HttpStatus.OK);
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<InlineResponse200>> alertsAlertIdIndicatorsGet(Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<List<InlineResponse200>> alertsAlertIdIndicatorsIndicatorIdEventsGet(Integer indicatorId, Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<List<InlineResponse2001>> alertsAlertIdIndicatorsIndicatorIdGet(Integer indicatorId, Integer alertId) {
        return null;
    }

    @Override
    public ResponseEntity<Alert> alertsAlertIdPatch(List<Patch> patch) {
        return null;
    }

    @Override
    public ResponseEntity<AlertsWrapper> searchAlerts(BigDecimal startTimeFrom, BigDecimal startTimeTo, String feedback, Integer minScore, Integer maxScore, List<String> tags, List<String> ids, String name, String indicatorsType, Integer limit, Integer offset, String sort) {
        return null;
    }

    private Alert setAlert(presidio.webapp.dto.Alert alertFromdb){
        Alert alert=new Alert();
        alert.setId(alertFromdb.getId());
        alert.setName(alertFromdb.getUsername());
        return alert;
    }
}
