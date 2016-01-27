package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationConfigurationRepository extends MongoRepository<ApplicationConfiguration, String>,ApplicationConfigurationRepositoryCustom {

	ApplicationConfiguration findOneByKey(String key);
	List<ApplicationConfiguration> findByKeyStartsWith(String key);

}