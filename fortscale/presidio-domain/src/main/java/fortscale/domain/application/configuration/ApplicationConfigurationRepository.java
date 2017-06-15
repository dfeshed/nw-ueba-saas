package fortscale.domain.application.configuration;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationConfigurationRepository extends MongoRepository<ApplicationConfiguration, String>,ApplicationConfigurationRepositoryCustom {

	ApplicationConfiguration findOneByKey(String key);
	List<ApplicationConfiguration> findByKeyStartsWith(String key);
	Long deleteByKey(String key);

}