package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface UserActivityRepository extends MongoRepository<UserActivityDocument, String>, UserActivityRepositoryCustom {

    List<UserActivityDocument> findAll();
}
