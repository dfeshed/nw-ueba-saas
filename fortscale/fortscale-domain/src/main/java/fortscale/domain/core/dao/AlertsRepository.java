package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Notification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlertsRepository extends MongoRepository<Alert, String>, AlertsRepositoryCustom {
    List<Alert> findAll(PageRequest request, int maxPages);
}
