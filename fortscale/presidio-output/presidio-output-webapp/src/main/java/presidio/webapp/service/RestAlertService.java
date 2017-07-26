package presidio.webapp.service;

import presidio.webapp.dto.Alert;
import presidio.webapp.filter.AlertFilter;

import java.util.List;

public interface RestAlertService {

    Alert getAlertById(String id);
    List<Alert> getAlerts(AlertFilter alertFilter);
}
