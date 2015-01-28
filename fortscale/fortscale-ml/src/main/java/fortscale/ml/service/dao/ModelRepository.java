package fortscale.ml.service.dao;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ModelRepository extends MongoRepository<Model, String>, ModelRepositoryCustom {
	
	/** Get a specific model for a given user */ 
	Model findByContextAndModelName(String context, String modelName);
}
