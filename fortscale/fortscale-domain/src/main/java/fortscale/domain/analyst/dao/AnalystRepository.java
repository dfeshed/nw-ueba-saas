package fortscale.domain.analyst.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.analyst.Analyst;




public interface AnalystRepository extends MongoRepository<Analyst, String>, PagingAndSortingRepository<Analyst, String> {

	Analyst findByUserName(String userName);
}
