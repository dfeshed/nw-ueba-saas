package fortscale.domain.analyst.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.analyst.Analyst;




public interface AnalystRepository extends MongoRepository<Analyst, String>, PagingAndSortingRepository<Analyst, String> {

	public Analyst findByUserName(String userName);
	public List<Analyst> findByIsDisabled(boolean isDisabled);
}
