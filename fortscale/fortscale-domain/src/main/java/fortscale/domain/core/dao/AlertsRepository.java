package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for Alerts
 */
public interface AlertsRepository extends MongoRepository<Alert, String>, AlertsRepositoryCustom {
    List<Alert> findByEntityName(String username);

}
