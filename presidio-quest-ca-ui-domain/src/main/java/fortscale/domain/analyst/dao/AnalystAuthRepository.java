package fortscale.domain.analyst.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.analyst.AnalystAuth;

public interface AnalystAuthRepository extends MongoRepository<AnalystAuth, String>, PagingAndSortingRepository<AnalystAuth, String> {

	AnalystAuth findByUsername(String username);
}
