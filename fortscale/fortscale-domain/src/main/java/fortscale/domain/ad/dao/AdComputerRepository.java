package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdComputer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdComputerRepository extends MongoRepository<AdComputer, String> {
	List<AdComputer> findByLastModifiedExists(boolean exists);
	long countByTimestampepoch(Long timestampepoch);
}
