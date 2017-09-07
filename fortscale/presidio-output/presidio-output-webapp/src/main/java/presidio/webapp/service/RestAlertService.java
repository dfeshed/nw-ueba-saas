package presidio.webapp.service;

import presidio.webapp.dto.Alert;

import java.util.List;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id);

    List<presidio.webapp.model.Alert> getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    Alert createResult(presidio.output.domain.records.alerts.Alert alertData);

    List<presidio.webapp.model.Alert> getAlertsByUserId(String userId);
}
