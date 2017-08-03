package presidio.webapp.controllers;

import fortscale.utils.logging.annotation.LogException;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.dto.Alert;
import presidio.webapp.dto.ListResponseBean;
import presidio.webapp.dto.SingleEntityResponseBean;
import presidio.webapp.restquery.RestAlertQuery;
import presidio.webapp.service.RestAlertService;

@RestController
@RequestMapping(value = "/alerts")
public class AlertsController {

    private final RestAlertService restAlertService;

    public AlertsController(RestAlertService restAlertService) {
        this.restAlertService = restAlertService;
    }

    @RequestMapping(value = "/{alertId}", method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public SingleEntityResponseBean<Alert> getAlertById(@PathVariable String alertId) {
        SingleEntityResponseBean<Alert> responseBean = new SingleEntityResponseBean<>();
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
    public ListResponseBean<Alert> getAlerts(RestAlertQuery restAlertQuery) {
        ListResponseBean<Alert> responseBean = new ListResponseBean<>();
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
