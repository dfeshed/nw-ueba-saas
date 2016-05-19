package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for Alerts
 */
public interface AlertsRepository extends MongoRepository<Alert, String>, AlertsRepositoryCustom {


}
