package fortscale.services;

import fortscale.domain.core.*;

import java.util.Date;
import java.util.List;

/**
 * Date: 6/23/2015.
 */
public interface EvidencesService {

	/**
	 * Create new object (transient) of evidence
	 * @param entityType        The type of the entity
	 * @param entityName        The name of the entity
	 * @param evidenceType    The type of evidence
	 * @param dataEntitiesIds   The relevant data sources
	 * @param score             The score
	 * @param anomalyValue        Value of the field
	 * @param evidenceTimeframe evidence timeframe
	 * @return					New evidence
	 */
	Evidence createTransientEvidence(EntityType entityType, String entityTypeFieldName, String entityName,
									 EvidenceType evidenceType, Date startDate, Date endDate,
									 List<String> dataEntitiesIds, Double score, String anomalyValue, String anomalyTypeFieldName,
									 Integer totalAmountOfEvents, EvidenceTimeframe evidenceTimeframe);

	/**
	 * Create tag evidence
	 * @param entityType
	 * @param entityTypeFieldName
	 * @param entityName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Evidence createTagEvidence(EntityType entityType, String entityTypeFieldName, String entityName, Long startDate,
			long endDate, String tag);

	/**
	 * Add supporting information data to the evidence
	 * @param evidence
	 */
	void setTagEvidenceSupportingInformationData(Evidence evidence);

	/**
	 * return userId from user service
	 * @param userName
	 * @return
	 */
	User getUserIdByUserName(String userName);


	/**
	 * Create new evidence in Mongo
	 * @param evidence	The evidence
	 */
	void saveEvidenceInRepository(Evidence evidence);

	/**
	 * Find all evidences for evidence type that their value matches one of the values in the comma separated string
	 * @param evidenceType	The evidence type
	 * @param anomalyValues	The anomaly value
	 * @return 				All the matching evidences
	 */
	List<Evidence> findByEvidenceTypeAndAnomalyValueIn(EvidenceType evidenceType, String[] anomalyValues);

	/**
	 * Find single evidence by ID
	 * @param id The ID string
	 * @return Single evidence
	 */
	public Evidence findById(String id);

	/**
	 * Find evidences from P and F features
	 * @param entityType
	 * @param entityName
	 * @param startDate
	 * @param endDate
	 * @param dataEntities
	 * @param featureName
	 * @return
	 */
	List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, long startDate, long endDate,
							String dataEntities, String featureName);

	/**
	 * Find evidences by start time, end time, type and entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
	List<Evidence> findByStartDateAndEndDateAndEvidenceTypeAndEntityName(long startDate, long endDate,
			String evidenceType, String entityName);

	/**
	 *
	 * @param afterDate
	 * @param beforeDate
	 * @param anomalyType
	 * @return
	 */
	List<Evidence> findByStartDateBetweenAndAnomalyTypeFieldName(Long afterDate, Long beforeDate, String anomalyType);

}
