package fortscale.domain.analyst.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.analyst.FortscaleConfiguration;

public interface FortscaleConfigurationRepository extends MongoRepository<FortscaleConfiguration, String>{
	public List<FortscaleConfiguration> findByConfigId(String configId, Pageable pageable);
	public FortscaleConfiguration findByConfigIdAndCreatedById(String configId, String createdById);
}
