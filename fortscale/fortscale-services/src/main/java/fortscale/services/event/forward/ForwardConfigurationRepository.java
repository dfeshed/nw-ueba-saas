package fortscale.services.event.forward;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForwardConfigurationRepository extends MongoRepository<ForwardConfiguration, String>{

	public ForwardConfiguration findByType(String type);
}