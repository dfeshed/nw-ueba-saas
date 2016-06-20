package fortscale.domain.system.dao;

import fortscale.domain.system.SystemConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigurationRepository extends MongoRepository<SystemConfiguration, String>{
	SystemConfiguration findByType(String type);
	
}