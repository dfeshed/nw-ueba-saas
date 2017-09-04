package presidio.webapp.controllers.alerts;

import org.apache.commons.collections.CollectionUtils;
import org.parboiled.common.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.InlineResponse200;
import presidio.webapp.model.InlineResponse2001;
import presidio.webapp.model.Patch;
import presidio.webapp.restquery.RestAlertQuery;
import presidio.webapp.service.RestAlertService;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        if (!StringUtils.isEmpty(alertId)) {
            Alert alert = restAlertService.getAlertById(alertId);
            if (alert != null) {
                return new ResponseEntity(alert, HttpStatus.OK);
            }
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<AlertsWrapper> searchAlerts(Integer pageSize, Integer pageNumber, BigDecimal startTimeFrom, BigDecimal startTimeTo, String feedback, Integer minScore, Integer maxScore, List<String> tags, List<String> ids, List<String> classification, List<String> indicatorsType, List<String> sort, String severity) {
        RestAlertQuery restAlertQuery = new RestAlertQuery();
        restAlertQuery.setClassification(classification);
        restAlertQuery.setSeverity(severity);
        if (!CollectionUtils.isEmpty(sort)) {
            List<Sort.Order> orders = new ArrayList<>();
            if (sort != null) {
                sort.forEach(s -> {
                    String[] params = s.split(":");
                    Sort.Direction direction = Sort.Direction.fromString(params[0]);
                    orders.add(new Sort.Order(direction, params[1]));

                });
            }
            restAlertQuery.setSort(new Sort(orders));
        }
        List<Alert> alerts = restAlertService.getAlerts(restAlertQuery);
        if (alerts != null) {
            AlertsWrapper alertsWrapper = new AlertsWrapper();
            alertsWrapper.setAlerts(alerts);
            alertsWrapper.setTotal(alerts.size());
            alertsWrapper.setPage(0);
            return new ResponseEntity(alertsWrapper, HttpStatus.OK);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }


}
