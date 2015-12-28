package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by avivs on 27/12/15.
 */
public interface ApplicationConfigurationRepository extends MongoRepository<ApplicationConfiguration, String>,ApplicationConfigurationRepositoryCustom {
}
