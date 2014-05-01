package fortscale.domain.system.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.system.SystemConfiguration;

public interface SystemConfigurationRepository extends MongoRepository<SystemConfiguration, String>{
	SystemConfiguration findByType(String type);
	
}