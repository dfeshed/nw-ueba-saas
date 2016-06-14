package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import fortscale.domain.core.UserScorePercentiles;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for Alerts
 */
public interface UserScorePercentilesRepository extends MongoRepository<UserScorePercentiles, String>{

    List<UserScorePercentiles> findByActive(boolean active);
}
