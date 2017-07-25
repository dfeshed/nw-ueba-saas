package presidio.webapp.controllers;

import fortscale.utils.logging.annotation.LogException;
import org.springframework.web.bind.annotation.*;
import presidio.webapp.dto.Alert;
import presidio.webapp.filter.AlertFilter;
import presidio.webapp.service.AlertService;

import java.util.List;

@RestController
@RequestMapping(value = "/alert")
public class AlertsController {

    private final AlertService alertService;

    public AlertsController(presidio.webapp.service.AlertService alertService) {
        this.alertService = alertService;
    }

    @RequestMapping(value = "/{alertId}", method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public Alert getAlertById(@PathVariable String alertId) {
        return alertService.getAlertById(alertId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public List<Alert> getAlerts(AlertFilter alertFilter) {
        return alertService.getAlerts(alertFilter);
    }
}
