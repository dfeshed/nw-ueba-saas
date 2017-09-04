package presidio.webapp.service;

import org.springframework.data.domain.Page;
import presidio.webapp.dto.Alert;
import presidio.webapp.restquery.RestAlertQuery;

import java.util.List;

public interface RestAlertService {

    presidio.webapp.model.Alert getAlertById(String id);

    List<presidio.webapp.model.Alert> getAlerts(RestAlertQuery restAlertQuery);

    Alert createResult(presidio.output.domain.records.alerts.Alert alertData);

    Page<presidio.webapp.model.Alert> getAlertsByUserId(String userId);
}
