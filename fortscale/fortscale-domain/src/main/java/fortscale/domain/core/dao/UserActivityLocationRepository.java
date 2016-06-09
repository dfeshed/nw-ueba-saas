package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivityLocationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface UserActivityLocationRepository extends MongoRepository<UserActivityLocationDocument, String>, UserActivityLocationRepositoryCustom {

    List<UserActivityLocationDocument> findAll();
}
