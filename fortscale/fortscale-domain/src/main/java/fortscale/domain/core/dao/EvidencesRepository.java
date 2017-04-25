package fortscale.domain.core.dao;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Repository for evidences
 * Date: 6/22/2015.
 */
public interface EvidencesRepository extends MongoRepository<Evidence,String>, EvidencesRepositoryCustom {

	/**
	 * Find single evidence by ID
	 * @param id The ID string
	 * @return Single evidence
	 */
	Evidence findById(String id);

	/**
	 * Find all evidences for specific entity according to it's ID
	 * @param entityName	The entity name
	 * @param entityType	The entity type
	 * @return All the matching evidences
	 */
	List<Evidence> findByEntityNameAndEntityType(String entityName, EntityType entityType);

	/**
	 * Find all evidences for evidence type that their value matches one of the values in the comma separated string
	 * @param evidenceType	The evidence type
	 * @param anomalyValues	The anomaly value
	 * @return All the matching evidences
	 */
	List<Evidence> findByEvidenceTypeAndAnomalyValueIn(EvidenceType evidenceType, String[] anomalyValues);

	/**
	 * Find all evidences that are in the time window, the same type and for the same entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
	List<Evidence> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(long startDate,
			long endDate, String evidenceType, String entityName);

	/**
	 * Find all evidences that are in the time window of end date, the same type and for the same entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
	List<Evidence> findByEndDateBetweenAndEvidenceTypeAndEntityName(long startDate,
			long endDate, String evidenceType, String entityName);

	List<Evidence> findByStartDateBetweenAndAnomalyTypeFieldName(Long afterDate, Long beforeDate, String anomalyType);

	List<Evidence> findByStartDateBetweenAndAnomalyTypeFieldNameAndEntityName(Long afterDate, Long beforeDate, String anomalyType, String entityName);

	List<Evidence> findByIdIn(String[] id);
}