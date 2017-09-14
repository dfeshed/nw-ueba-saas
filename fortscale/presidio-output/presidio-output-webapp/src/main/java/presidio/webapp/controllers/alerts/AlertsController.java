package presidio.webapp.controllers.alerts;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.model.*;
import org.springframework.web.bind.annotation.PathVariable;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertQuery;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.service.RestAlertService;

import java.util.List;

@Controller
public class AlertsController implements AlertsApi {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @Override
    public ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return", required = true) @PathVariable("alertId") String alertId,
                                          @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue="false") Boolean expand)  {
        Alert alert = restAlertService.getAlertById(alertId, expand);
        HttpStatus httpStatus = alert != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity(alert, httpStatus);
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlerts(AlertQuery alertQuery) {
        return new ResponseEntity(restAlertService.getAlerts(alertQuery), HttpStatus.OK);
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


    public ResponseEntity<EventsWrapper> getIndicatorEventsByAlert(@ApiParam(value = "The ID of the indicator to return",required=true ) @PathVariable("indicatorId") String indicatorId,
                                                                    @ApiParam(value = "The ID of the alert to return",required=true ) @PathVariable("alertId") String alertId,
                                                                     EventQuery eventQuery) {
        List<Event> events = restAlertService.getIndicatorEventsByIndicatorId(indicatorId, eventQuery);
        if (events != null) {
            EventsWrapper eventsWrapper = new EventsWrapper();
            eventsWrapper.setEvents(events);
            eventsWrapper.setTotal(events.size());
            eventsWrapper.setPage(0);
            return new ResponseEntity(eventsWrapper, HttpStatus.OK);
        }
        return new ResponseEntity(null, HttpStatus.OK);
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
