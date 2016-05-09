package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.UserSupportingInformationService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Services for managing the evidences
 *
 * Date: 6/23/2015.
 */
@Service("evidencesService")
public class EvidencesServiceImpl implements EvidencesService, InitializingBean {

	final String TAG_ANOMALY_TYPE_FIELD_NAME = "tag";
	final String TAG_DATA_ENTITY ="active_directory";


	/**
	 * Mongo repository for evidences
	 */
	@Autowired
	private EvidencesRepository evidencesRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSupportingInformationService userSupportingInformationService;

	// Severity thresholds for evidence
	@Value("${evidence.severity.medium:80}")
	protected int medium;
	@Value("${evidence.severity.high:90}")
	protected int high;
	@Value("${evidence.severity.critical:95}")
	protected int critical;

	@Value("${collection.evidence.tag.score:50}")
	protected double tagScore;

	/**
	 * Keeps mapping between score and severity
	 */
	private NavigableMap<Integer,Severity> scoreToSeverity = new TreeMap<>();


	@Override
	public void afterPropertiesSet() throws Exception {
		// init scoring to severity map
		scoreToSeverity.put(0, Severity.Low);
		scoreToSeverity.put(medium, Severity.Medium);
		scoreToSeverity.put(high, Severity.High);
		scoreToSeverity.put(critical, Severity.Critical);
	}


	@Override
	public Evidence createTransientEvidence(EntityType entityType, String entityTypeFieldName, String entityName,
											EvidenceType evidenceType, Date startDate, Date endDate,
											List<String> dataEntitiesIds, Double score, String anomalyValue,
											String anomalyTypeFieldName, Integer totalAmountOfEvents, EvidenceTimeframe evidenceTimeframe) {

		// casting score to int
		int intScore = score.intValue();

		// calculate severity
		Severity severity = scoreToSeverity.get(scoreToSeverity.floorKey(intScore));

		// create new transient evidence (do not save to Mongo yet)
		return new Evidence(entityType, entityTypeFieldName, entityName, evidenceType, startDate.getTime(),
				endDate.getTime(), anomalyTypeFieldName, anomalyValue, dataEntitiesIds, intScore, severity,
				totalAmountOfEvents, evidenceTimeframe);
	}

	@Override public Evidence createTagEvidence(EntityType entityType, String entityTypeFieldName, String entityName,
			Long startDate, long endDate, String tag){

		// Create data entities array for tag evidence with constant value
		List<String> dataEntitiesIds = new ArrayList<>();
		dataEntitiesIds.add(TAG_DATA_ENTITY);

		Evidence evidence = createTransientEvidence(entityType, entityTypeFieldName, entityName, EvidenceType.Tag,
				new Date(startDate), new Date(endDate), dataEntitiesIds, tagScore, tag,
				TAG_ANOMALY_TYPE_FIELD_NAME, 0, null);

		setTagEvidenceSupportingInformationData(evidence);

		// Save evidence to MongoDB
		saveEvidenceInRepository(evidence);

		return evidence;
	}

	@Override public void setTagEvidenceSupportingInformationData(Evidence evidence){
		User user = getUserIdByUserName(evidence.getEntityName());
		EntitySupportingInformation entitySupportingInformation =  userSupportingInformationService.createUserSupportingInformation(user, userService);

		evidence.setSupportingInformation(entitySupportingInformation);
	}

	public User getUserIdByUserName(String userName) {
		return userService.findByUsername(userName);
	}

	@Override
	public void saveEvidenceInRepository(Evidence evidence) {
		saveEvidence(evidence);
	}

	@Override
	public List<Evidence> findByEvidenceTypeAndAnomalyValueIn(EvidenceType evidenceType, String[] anomalyValues) {
		return evidencesRepository.findByEvidenceTypeAndAnomalyValueIn(evidenceType, anomalyValues);
	}

	@Override
	public Evidence findById(String id) {
		return evidencesRepository.findById(id);
	}

	/**
	 * Finds evidences in mongo based on entity, time and type of feature
	 * @param entityEvent
	 * @param entityName
	 * @param startDate - the end time of the feature would be equal or after that startDate
	 * @param endDate - the end time of the feature would be equal or before that endDate
	 * @param dataEntities
	 * @param featureName
	 * @return
	 */
	public List<Evidence> findFeatureEvidences(EntityType entityEvent, String entityName, long startDate, long endDate,
			String dataEntities, String featureName) {
		return evidencesRepository.findFeatureEvidencesByFeatureEndTime(entityEvent, entityName, startDate, endDate, dataEntities, featureName);
	}

	public  List<Evidence> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(
			long startDate, long endDate, String evidenceType, String entityName) {
		return evidencesRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(startDate, endDate, evidenceType, entityName);
	}

	public  List<Evidence> findByEndDateBetweenAndEvidenceTypeAndEntityName(
			long startDate, long endDate, String evidenceType, String entityName) {
		return evidencesRepository.findByEndDateBetweenAndEvidenceTypeAndEntityName(startDate, endDate, evidenceType, entityName);
	}

	public List<Evidence> findEvidence(Long afterDate, Long beforeDate, String anomalyType, String entityName){
		if (StringUtils.isBlank(entityName)){
			return evidencesRepository.findByStartDateBetweenAndAnomalyTypeFieldName(TimestampUtils.normalizeTimestamp(afterDate), TimestampUtils.normalizeTimestamp(beforeDate), anomalyType);
		} else {
			return evidencesRepository.findByStartDateBetweenAndAnomalyTypeFieldNameAndEntityName(TimestampUtils.normalizeTimestamp(afterDate),
					TimestampUtils.normalizeTimestamp(beforeDate), anomalyType, entityName);

		}

	}

	/**
	 * Saves evidence in Mongo
	 * @param evidence the evidence to save
	 * @return the saved evidence
	 */
	private Evidence saveEvidence(Evidence evidence){
		return evidencesRepository.save(evidence);
	}


	public long count(long fromTime, long toTime){
		return  evidencesRepository.countWithParameters(fromTime, toTime);
	}

	@Override
	public List getDistinctByFieldName(String fieldName) {

		return evidencesRepository.getDistinctByFieldName(fieldName);

	}

	@Override
	public List<String> getEvidenceIdsByAnomalyTypeFiledNames(List<DataSourceAnomalyTypePair> anomalyTypesList) {
		return evidencesRepository.getEvidenceIdsByAnomalyTypeFiledNames(anomalyTypesList);
	}

	@Override
	public List<String> getDistinctAnomalyType() {
		return evidencesRepository.getDistinctAnomalyType();
	}

}
