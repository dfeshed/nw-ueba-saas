package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdOU;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdOURepository extends MongoRepository<AdOU, String>, AdOURepositoryCustom {
	List<AdOU> findByLastModifiedExists(boolean exists);
	long countByTimestampepoch(Long timestampepoch);
	List<AdOU> findByOuLikeIgnoreCase(String contains);
}
