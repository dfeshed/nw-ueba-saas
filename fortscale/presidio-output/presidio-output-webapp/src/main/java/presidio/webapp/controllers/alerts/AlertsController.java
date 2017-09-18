package presidio.webapp.controllers.alerts;

import fortscale.utils.logging.Logger;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertQuery;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Event;
import presidio.webapp.model.EventQuery;
import presidio.webapp.model.EventsWrapper;
import presidio.webapp.model.Indicator;
import presidio.webapp.model.IndicatorQuery;
import presidio.webapp.model.IndicatorsWrapper;
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
    public ResponseEntity<Alert> getAlert(@ApiParam(value = "The UUID of the alert to return", required = true) @PathVariable("alertId") String alertId,
                                          @ApiParam(value = "Flag to say if needed indicators or not", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        try {
            Alert alert = restAlertService.getAlertById(alertId, expand);
            HttpStatus httpStatus = alert != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(alert, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get alert by alertId:{} , But got internal error {}", alertId, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlerts(AlertQuery alertQuery) {
        try {
            AlertsWrapper alertsWrapper = restAlertService.getAlerts(alertQuery);
            HttpStatus httpStatus = alertsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return new ResponseEntity(alertsWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get alerts with this alertQuery:{} , But got internal error {}", alertQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Indicator> getIndicatorByAlert(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") String indicatorId,
                                                         @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") String alertId,
                                                         @ApiParam(value = "", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {

        try {
            Indicator indicator = restAlertService.getIndicatorById(indicatorId, expand);
            HttpStatus httpStatus = indicator != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(indicator, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get indicator by indicatorId:{} , But got internal error {}", indicatorId, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<EventsWrapper> getIndicatorEventsByAlert(@ApiParam(value = "The ID of the indicator to return", required = true) @PathVariable("indicatorId") String indicatorId,
                                                                   @ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") String alertId,
                                                                   EventQuery eventQuery) {
        try {
            EventsWrapper eventsWrapper = restAlertService.getIndicatorEventsByIndicatorId(indicatorId, eventQuery);
            HttpStatus httpStatus = eventsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return new ResponseEntity(eventsWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get events with this eventQuery:{} , But got internal error {}", eventQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<IndicatorsWrapper> getIndicatorsByAlert(@ApiParam(value = "The ID of the alert to return", required = true) @PathVariable("alertId") String alertId,
                                                                  IndicatorQuery indicatorQuery) {
        try {
            IndicatorsWrapper indicatorsWrapper = restAlertService.getIndicatorsByAlertId(alertId, indicatorQuery);
            HttpStatus httpStatus = indicatorsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
            return new ResponseEntity(indicatorsWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying the to get indicators with this indicatorQuery:{} , But got internal error {}", indicatorQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
