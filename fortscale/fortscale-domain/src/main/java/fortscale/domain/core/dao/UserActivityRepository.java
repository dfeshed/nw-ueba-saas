package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    UserActivity findOneByKey(String key);
    List<UserActivity> findByKeyStartsWith(String key);
}
