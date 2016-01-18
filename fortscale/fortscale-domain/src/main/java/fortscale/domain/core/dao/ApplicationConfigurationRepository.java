package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationConfigurationRepository extends MongoRepository<ApplicationConfiguration, String>,ApplicationConfigurationRepositoryCustom {

	ApplicationConfiguration findOneByKey(String key);

}