package fortscale.domain.core.dao;

import fortscale.domain.core.Evidence;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for evidences
 * Date: 6/22/2015.
 */
public interface EvidencesRepository extends MongoRepository<Evidence,String> {

	/**
	 * Find single evidence by ID
	 * @param id The ID string
	 * @return Single evidence
	 */
	public Evidence findById(String id);

	/**
	 * Find all evidences for specific entity according to it's ID
	 * @param entityMongoId	The entity ID
	 * @return All the matching evidences
	 */
	public List<Evidence> findByEntityMongoId(String entityMongoId);
}
