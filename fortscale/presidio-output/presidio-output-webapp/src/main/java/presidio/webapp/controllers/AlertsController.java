package presidio.webapp.controllers;

import fortscale.utils.logging.annotation.LogException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.dto.AlertListEntityResponseBean;
import presidio.webapp.dto.AlertSingleEntityResponseBean;
import presidio.webapp.restquery.RestAlertQuery;
import presidio.webapp.service.RestAlertService;

@RestController
@RequestMapping(value = "/alerts")
@Api(value = "AlertController", description = "Alert Controller")
public class AlertsController implements AlertsControllerApi {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @RequestMapping(value = "/{alertId}", method = RequestMethod.GET)
    @ResponseBody
    @LogException
//    @ApiOperation(value = "Return alert by alert id", response = AlertSingleEntityResponseBean.class, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "Return alert by alert id", response = AlertSingleEntityResponseBean.class, consumes = "application/json", produces = "application/json")
    public AlertSingleEntityResponseBean getAlertById(@ApiParam(name = "alertId", value = "The ID of the alert") @PathVariable String alertId) {
        AlertSingleEntityResponseBean responseBean = new AlertSingleEntityResponseBean();
        try {
            responseBean.setData(restAlertService.getAlertById(alertId));

        }catch (Exception e){
            responseBean.setErrorMessage(String.format("Got error while getting alert by id - %s", e.getMessage()));
            responseBean.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        return responseBean;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    @ApiOperation(value = "Return alerts", response = AlertListEntityResponseBean.class)
    public AlertListEntityResponseBean getAlerts(@ApiParam(name = "alertQuery2", value = "Filter for Alerts") RestAlertQuery restAlertQuery) {
        AlertListEntityResponseBean responseBean = new AlertListEntityResponseBean();
        try {
            responseBean.setData(restAlertService.getAlerts(restAlertQuery));
            responseBean.setPage(restAlertQuery.getPageNumber());
        }catch (Exception e){
            responseBean.setErrorMessage(String.format("Got error while getting alert by filter - %s", e.getMessage()));
            responseBean.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        return responseBean;
    }
}
