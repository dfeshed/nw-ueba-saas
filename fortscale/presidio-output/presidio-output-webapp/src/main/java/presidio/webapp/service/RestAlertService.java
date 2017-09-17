package presidio.webapp.service;


import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RestAlertService {

    Alert getAlertById(String id);

    AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    AlertsWrapper getAlertsByUserId(String userId);

    Map<String, List<Alert>> getAlertsByUsersIds(Collection<String> userId);
}
