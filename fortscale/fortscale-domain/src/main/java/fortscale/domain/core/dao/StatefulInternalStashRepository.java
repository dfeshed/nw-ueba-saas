package fortscale.domain.core.dao;

import fortscale.domain.core.StatefulInternalStash;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatefulInternalStashRepository extends MongoRepository<StatefulInternalStash, String>, StatefulInternalStashRepositoryCustom {}