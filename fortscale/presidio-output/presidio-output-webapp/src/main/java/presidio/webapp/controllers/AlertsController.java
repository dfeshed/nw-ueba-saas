package presidio.webapp.controllers;

import fortscale.utils.logging.annotation.LogException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.dto.Alert;
import presidio.webapp.dto.AlertListEntityResponseBean;
import presidio.webapp.dto.AlertSingleEntityResponseBean;
import presidio.webapp.restquery.RestAlertQuery;
import presidio.webapp.service.RestAlertService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/alerts")
@Api(value = "AlertController", description = "Alert Controller")
public class AlertsController {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @RequestMapping(value = "/{alertId}", method = RequestMethod.GET)
    @ResponseBody
    @LogException
    @ApiOperation(value = "Return alert by alert id", response = AlertSingleEntityResponseBean.class)
    public AlertSingleEntityResponseBean getAlertById(@ApiParam(name = "alertId", value = "The ID of the alert") @PathVariable String alertId) {
        AlertSingleEntityResponseBean responseBean = new AlertSingleEntityResponseBean();
        try {
            responseBean.setData(restAlertService.getAlertById(alertId));
            responseBean.setStatus(HttpStatus.SC_OK);

        } catch (Exception e) {
            responseBean.setErrorMessage(String.format("Got error while getting alert by id - %s", e.getMessage()));
            responseBean.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        return responseBean;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    @ApiOperation(value = "Return alerts", response = AlertListEntityResponseBean.class)
    public AlertListEntityResponseBean getAlerts(@ApiParam(name = "alertQuery", value = "Filter for Alerts") RestAlertQuery restAlertQuery) {
        AlertListEntityResponseBean responseBean = new AlertListEntityResponseBean();
        try {
            Page<presidio.output.domain.records.alerts.Alert> alertsPage = restAlertService.getAlerts(restAlertQuery);

            List<Alert> alerts = new ArrayList<>();
            if (alertsPage.hasContent()) {
                alertsPage.forEach(alert -> alerts.add(restAlertService.createResult(alert)));
            }

            responseBean.setData(alerts);
            responseBean.setPage(restAlertQuery.getPageNumber());
            responseBean.setStatus(HttpStatus.SC_OK);
            responseBean.setTotal(alertsPage.getTotalElements());
        } catch (Exception e) {
            responseBean.setErrorMessage(String.format("Got error while getting alert by filter - %s", e.getMessage()));
            responseBean.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        return responseBean;
    }
}
