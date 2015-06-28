package fortscale.domain.core.dao;

import fortscale.domain.core.DeletedUser;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by idanp on 6/16/2015.
 */
public interface DeletedUserRepository extends MongoRepository<DeletedUser, String> {
}
