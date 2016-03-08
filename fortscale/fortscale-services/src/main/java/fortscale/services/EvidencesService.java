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
			long endDate, String tag, User user, UserService userService);



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
	List<Evidence> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(long startDate,
			long endDate, String evidenceType, String entityName);

	/**
	 * Find evidences by start time, end time, type and entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
	List<Evidence> findByEndDateBetweenAndEvidenceTypeAndEntityName(long startDate,
			long endDate, String evidenceType, String entityName);

	/**
	 *
	 * @param afterDate
	 * @param beforeDate
	 * @param anomalyType
	 * @param entityName
	 * @return
	 */
	public List<Evidence> findEvidence(Long afterDate, Long beforeDate, String anomalyType, String entityName);


	/**
	 * Count all evidences by filter.
	 * Currently the filter contain the fromTime only.
	 * Might be expand when needed
	 * @param fromTime - the time which the evidence start time should be greated or equals to
	 * @param toTime - the time which the evidence start time should be smaller or equals to
	 * @return
	 */
	public long count(long fromTime, long toTime);

	/**
	 *
	 * @param fieldName the field name to get distinct values by.
	 * @return A map of distinct keys
     */
	public List getDistinctByFieldName (String fieldName);

	public List<String> getEvidenceIdsByAnomalyTypeFiledNames(List<DataSourceAnomalyTypePair> anomalyTypesList);

	public List<String> getDistinctAnomalyType();


	/**
	 * Count how many evidenc took place acocrding to the filter (which one or two country-city and for specific user)
	 * The second country-city and the user is optional
	 * @param indicatorStartTime
	 * @param country1 -
	 * @param city1
	 * @param country2 - (optional)
	 * @param city2 - (optional)
	 * @param username - the normalized user name of the user (optional)
	 * @return number of indicators which match to criteria
	 */
	public int getVpnGeoHoppingCount(long indicatorStartTime, String country1, String city1, String country2, String city2, String username);
}
