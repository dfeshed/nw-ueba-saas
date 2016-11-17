package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdOURepository extends MongoRepository<AdOU, String>{
	List<AdOU> findByLastModifiedExists(boolean exists);
	long countByTimestampepoch(Long timestampepoch);
	List<AdGroup> findByNameStartingWithIgnoreCase(String startsWith);
	List<AdOU> findByOuStartingWithIgnoreCase(String startsWith);
}
