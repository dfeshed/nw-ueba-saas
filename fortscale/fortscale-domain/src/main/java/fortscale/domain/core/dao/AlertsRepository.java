package fortscale.domain.core.dao;

import fortscale.domain.core.dao.rest.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import javax.servlet.http.HttpServletRequest;

public interface AlertsRepository extends MongoRepository<Alert, String>, AlertsRepositoryCustom {
}
