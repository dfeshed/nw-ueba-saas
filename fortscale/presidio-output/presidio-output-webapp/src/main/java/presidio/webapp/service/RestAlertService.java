package presidio.webapp.service;

import presidio.webapp.model.AlertQueryEnums;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.EventsWrapper;
import presidio.webapp.model.IndicatorsWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id, boolean expand);

    AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    AlertsWrapper getAlertsByUserId(String userId, boolean expand);

    Map<String, List<presidio.webapp.model.Alert>> getAlertsByUsersIds(Collection<String> userId);

    presidio.webapp.model.Indicator getIndicatorById(String indicatorId, boolean expand);

    IndicatorsWrapper getIndicatorsByAlertId(String alertId, presidio.webapp.model.IndicatorQuery indicatorQuery);

    EventsWrapper getIndicatorEventsByIndicatorId(String indicatorId, presidio.webapp.model.EventQuery eventQuery);

    void updateAlertFeedback(String alertId, AlertQueryEnums.AlertFeedback feedback);
}
