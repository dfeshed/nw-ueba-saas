package presidio.webapp.controllers;

import fortscale.utils.logging.annotation.LogException;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.dto.Alert;
import presidio.webapp.dto.ListResponseBean;
import presidio.webapp.dto.SingleEntityResponseBean;
import presidio.webapp.filter.AlertFilter;
import presidio.webapp.service.RestAlertService;

@RestController
@RequestMapping(value = "/alert")
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
        responseBean.setData(restAlertService.getAlertById(alertId));
        return responseBean;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public ListResponseBean<Alert> getAlerts(AlertFilter alertFilter) {
        ListResponseBean<Alert> responseBean = new ListResponseBean<>();
        responseBean.setData(restAlertService.getAlerts(alertFilter));
        responseBean.setPage(alertFilter.getPageNumber());
        return responseBean;
    }
}
