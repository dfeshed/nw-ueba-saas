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

    AlertsWrapper getAlertsByEntityId(String entityId, boolean expand);

    Map<String, List<presidio.webapp.model.Alert>> getAlertsByEntityIds(Collection<String> entityIds);

    presidio.webapp.model.Indicator getIndicatorById(String indicatorId, boolean expand);

    IndicatorsWrapper getIndicatorsByAlertId(String alertId, presidio.webapp.model.IndicatorQuery indicatorQuery);

    EventsWrapper getIndicatorEventsByIndicatorId(String indicatorId, presidio.webapp.model.EventQuery eventQuery);

    void updateAlertFeedback(List<String> alertIds, AlertQueryEnums.AlertFeedback feedback);
}
