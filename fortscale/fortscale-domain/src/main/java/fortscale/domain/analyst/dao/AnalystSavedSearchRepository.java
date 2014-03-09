package fortscale.domain.analyst.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.analyst.AnalystSavedSearch;

public interface AnalystSavedSearchRepository extends MongoRepository<AnalystSavedSearch, String>{
	
	public AnalystSavedSearch findByNameAndCategory(String name, String category); 
}
