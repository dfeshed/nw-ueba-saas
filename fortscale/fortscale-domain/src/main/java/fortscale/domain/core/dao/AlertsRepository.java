package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Repository for Alerts
 */
public interface AlertsRepository extends MongoRepository<Alert, String>, AlertsRepositoryCustom {


}
