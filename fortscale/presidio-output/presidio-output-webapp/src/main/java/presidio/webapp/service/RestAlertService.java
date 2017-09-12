package presidio.webapp.service;

import presidio.webapp.dto.Alert;

import java.util.Collection;
import java.util.List;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id, boolean expand);

    List<presidio.webapp.model.Alert> getAlerts(presidio.webapp.model.AlertQuery alertQuery);

    Alert createResult(presidio.output.domain.records.alerts.Alert alertData);

    List<presidio.webapp.model.Alert> getAlertsByUserId(String userId, boolean expand);

    List<presidio.webapp.model.Alert> getAlertsByUsersIds(Collection<String> userId);

    presidio.webapp.model.Indicator getIndicatorById(String indicatorId, boolean expand);

    List<presidio.webapp.model.Indicator> getIndicatorsByAlertId(String alertId, presidio.webapp.model.IndicatorQuery indicatorQuery);
}
