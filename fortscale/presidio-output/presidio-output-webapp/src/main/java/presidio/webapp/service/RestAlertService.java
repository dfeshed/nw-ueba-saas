package presidio.webapp.service;

import presidio.webapp.dto.Alert;
import presidio.webapp.model.AlertsWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id);

    AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    Alert createResult(presidio.output.domain.records.alerts.Alert alertData);

    AlertsWrapper getAlertsByUserId(String userId);

    Map<String, List<presidio.webapp.model.Alert>> getAlertsByUsersIds(Collection<String> userId);
}
