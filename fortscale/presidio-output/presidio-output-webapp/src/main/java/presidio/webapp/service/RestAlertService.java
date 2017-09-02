package presidio.webapp.service;

import org.springframework.data.domain.Page;
import presidio.webapp.dto.Alert;
import presidio.webapp.restquery.RestAlertQuery;

public interface RestAlertService {

    Alert getAlertById(String id);

    Page<presidio.output.domain.records.alerts.Alert> getAlerts(RestAlertQuery restAlertQuery);

    Alert createResult(presidio.output.domain.records.alerts.Alert alertData);

    Page<presidio.webapp.model.Alert> getAlertsByUserId(String userId);
}
