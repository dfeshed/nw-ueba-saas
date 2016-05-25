package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivityLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author gils
 * 25/05/2016
 */
public interface UserActivityLocationsRepository extends MongoRepository<UserActivityLocation,String>{
    UserActivityLocation findById(String id);
}
