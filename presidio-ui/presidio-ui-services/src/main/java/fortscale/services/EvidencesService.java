package fortscale.services;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Events;
import fortscale.domain.dto.DateRange;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Date: 6/23/2015.
 */
public interface EvidencesService {

//	/**
//	 * Create new object (transient) of evidence
//	 * @param entityType        The type of the entity
//	 * @param entityName        The name of the entity
//	 * @param evidenceType    The type of evidence
//	 * @param dataEntitiesIds   The relevant data sources
//	 * @param score             The score
//	 * @param anomalyValue        Value of the field
//	 * @param evidenceTimeframe evidence timeframe
//	 * @return					New evidence
//	 */
//	Evidence createTransientEvidence(EntityType entityType, String entityTypeFieldName, String entityName,
//									 EvidenceType evidenceType, Date startDate, Date endDate,
//									 List<String> dataEntitiesIds, Double score, String anomalyValue, String anomalyTypeFieldName,
//									 Integer totalAmountOfEvents, EvidenceTimeframe evidenceTimeframe);

//	/**
//	 * Create tag evidence
//	 * @param entityType
//	 * @param entityTypeFieldName
//	 * @param entityName
//	 * @param startDate
//	 * @param endDate
//	 * @return
//	 */
//	Evidence createTagEvidence(EntityType entityType, String entityTypeFieldName, String entityName, Long startDate,
//			long endDate, String tag);

	/**
	 * Add supporting information data to the evidence
	 * @param evidence
	 */
//	void setTagEvidenceSupportingInformationData(Evidence evidence);

	/**
	 * return userId from user service
	 * @param userName
	 * @return
	 */
//	User getUserIdByUserName(String userName);


	/**
	 * Create new evidence in Mongo
	 * @param evidence	The evidence
	 */
	void saveEvidenceInRepository(Evidence evidence);

	List<SupportingInformationGenericData> getSupportingInformationIndicatorId(String indicatorId);

//	/**
//	 * Find all evidences for evidence type that their value matches one of the values in the comma separated string
//	 * @param evidenceType	The evidence type
//	 * @param anomalyValues	The anomaly value
//	 * @return 				All the matching evidences
//	 */
//	List<Evidence> findByEvidenceTypeAndAnomalyValueIn(EvidenceType evidenceType, String[] anomalyValues);

	/**
	 * Find single evidence by ID
	 * @param id The ID string
	 * @return Single evidence
	 */
	public Evidence findById(String id);

//	/**
//	 * Find evidences from P and F features
//	 * @param entityType
//	 * @param entityName
//	 * @param endDateRange
//	 * @param dataEntities
//	 * @param featureName
//	 * @return
//	 */
//	List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, DateRange endDateRange,
//							String dataEntities, String featureName);

	/**
	 * Find evidences by start time, end time, type and entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
//	List<Evidence> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(long startDate,
//			long endDate, String evidenceType, String entityName);

	/**
	 * Find evidences by start time, end time, type and entity
	 * @param startDate
	 * @param endDate
	 * @param evidenceType
	 * @param entityName
	 * @return
	 */
//	List<Evidence> findByEndDateBetweenAndEvidenceTypeAndEntityName(long startDate,
//			long endDate, String evidenceType, String entityName);

	/**
	 *
	 * @param anomalyType
	 * @param entityName
	 * @return
	 */
//	public List<Evidence> findEvidence(DateRange dateRange, String anomalyType, String entityName);


	/**
	 * Count all evidences by filter.
	 * Currently the filter contain the fromTime only.
	 * Might be expand when needed
	 * @param fromTime - the time which the evidence start time should be greated or equals to
	 * @param toTime - the time which the evidence start time should be smaller or equals to
	 * @return
	 */
	long count(long fromTime, long toTime);

	/**
	 *
	 * @param fieldName the field name to get distinct values by.
	 * @return A map of distinct keys
	 */
	List getDistinctByFieldName (String fieldName);

//	 List<String> getDistinctAnomalyType();

	List<Evidence> getEvidencesById(List<String> evidenceIds);


	Events getListOfEvents(boolean requestTotal, boolean useCache, Integer page,
						   Integer size, String sortField, String sortDirection, String evidenceId);
}