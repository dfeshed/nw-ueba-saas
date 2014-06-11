package fortscale.streaming.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ModelRepository extends MongoRepository<Model, String>, ModelRepositoryCustom {
	
	/** Get a specific model for a given user */ 
	Model findByUserNameAndModelName(String userName, String modelName);
	
	/** Get all models by model type, with pagination support */
	Page<Model> findByModelName(String modelName, Pageable pageable);
}
