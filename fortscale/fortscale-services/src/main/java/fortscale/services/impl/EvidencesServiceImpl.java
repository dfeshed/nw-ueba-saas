package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.EvidencesService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Services for managing the evidences
 *
 * Date: 6/23/2015.
 */
@Service("evidencesService")
public class EvidencesServiceImpl implements EvidencesService, InitializingBean {

	/**
	 * Mongo repository for evidences
	 */
	@Autowired
	private EvidencesRepository evidencesRepository;


	// Severity thresholds for evidence
	@Value("${evidence.severity.medium:80}")
	protected int medium;
	@Value("${evidence.severity.high:90}")
	protected int high;
	@Value("${evidence.severity.critical:95}")
	protected int critical;

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

	public Evidence findFEvidence(EntityType entityEvent, String entityName, long startDate, long endDate,
			String dataEntities, String featureName) {
		return evidencesRepository.findFEvidence(entityEvent, entityName, startDate, endDate, dataEntities, featureName);
	}

	/**
	 * Saves evidence in Mongo
	 * @param evidence the evidence to save
	 * @return the saved evidence
	 */
	private Evidence saveEvidence(Evidence evidence){
		return evidencesRepository.save(evidence);
	}

}
