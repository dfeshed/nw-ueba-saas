package fortscale.domain.analyst.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.analyst.AnalystFollowUser;

public interface AnalystFollowUserRepository extends MongoRepository<AnalystFollowUser, String>{

}
