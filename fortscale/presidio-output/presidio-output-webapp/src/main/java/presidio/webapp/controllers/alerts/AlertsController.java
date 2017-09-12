package presidio.webapp.controllers.alerts;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                          @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue="false") Boolean expand) {
        if (!StringUtils.isEmpty(alertId)) {
            Alert alert = restAlertService.getAlertById(alertId, expand);
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
    public  ResponseEntity<Indicator> getIndicatorByAlert(@ApiParam(value = "The ID of the indicator to return",required=true ) @PathVariable("indicatorId") String indicatorId,
                                                          @ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                                          @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue="false") Boolean expand) {
        if (!StringUtils.isEmpty(alertId) && !StringUtils.isEmpty(indicatorId)) {
            Indicator indicator = restAlertService.getIndicatorById(indicatorId, expand);
            if (indicator != null) {
                return new ResponseEntity(indicator, HttpStatus.OK);
            }
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<IndicatorsWrapper> getIndicatorsByAlert(@ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                                                  IndicatorQuery indicatorQuery) {
        List<Indicator> indicators = restAlertService.getIndicatorsByAlertId(alertId, indicatorQuery);
        if (indicators != null) {
            IndicatorsWrapper indicatorsWrapper = new IndicatorsWrapper();
            indicatorsWrapper.setIndicators(indicators);
            indicatorsWrapper.setTotal(indicators.size());
            indicatorsWrapper.setPage(indicatorQuery.getPageNumber());
            return new ResponseEntity(indicatorsWrapper, HttpStatus.OK);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }

}
