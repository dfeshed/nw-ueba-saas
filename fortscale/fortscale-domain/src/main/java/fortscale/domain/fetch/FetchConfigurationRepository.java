package fortscale.domain.fetch;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FetchConfigurationRepository extends MongoRepository<FetchConfiguration, String> {

	FetchConfiguration findByType(String type);
}
