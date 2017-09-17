package presidio.webapp.service;

import presidio.webapp.dto.Alert;
import presidio.webapp.model.AlertsWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id, boolean expand);

    AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    AlertsWrapper getAlertsByUserId(String userId);

    Map<String, List<presidio.webapp.model.Alert>> getAlertsByUsersIds(Collection<String> userId);

    presidio.webapp.model.Indicator getIndicatorById(String indicatorId, boolean expand);

    List<presidio.webapp.model.Indicator> getIndicatorsByAlertId(String alertId, presidio.webapp.model.IndicatorQuery indicatorQuery);

    List<presidio.webapp.model.Event> getIndicatorEventsByIndicatorId(String indicatorId, presidio.webapp.model.EventQuery eventQuery);
}
