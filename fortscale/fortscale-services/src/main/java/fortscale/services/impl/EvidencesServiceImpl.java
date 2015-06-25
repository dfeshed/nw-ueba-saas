package fortscale.services.impl;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceSeverity;
import fortscale.domain.core.dao.EvidencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Services for managing the evidences
 *
 * Date: 6/23/2015.
 */
@Service("EvidencesService")
public class EvidencesServiceImpl implements EvidencesService {

	/**
	 * Mongo repository for evidences
	 */
	@Autowired
	private EvidencesRepository evidencesRepository;

	/**
	 * Keeps mapping between score and severity
	 */
	private static NavigableMap<Integer,EvidenceSeverity> scoreToSeverity = new TreeMap<>();

	static {
		// init scoring
		scoreToSeverity.put(0, EvidenceSeverity.Low);
		scoreToSeverity.put(85, EvidenceSeverity.Medium);
		scoreToSeverity.put(90, EvidenceSeverity.High);
		scoreToSeverity.put(95, EvidenceSeverity.Critical);
	}


	@Override
	public Evidence createTransientEvidence(EntityType entityType, String entityName, Date date,
			String scoreFieldName, String classifier, Double score) {

		// casting score to int
		int intScore = score.intValue();

		// calculate severity
		EvidenceSeverity severity = scoreToSeverity.get(scoreToSeverity.floorKey(intScore));

		// TODO choose type according to score
		String evidenceType = scoreFieldName;

		return new Evidence(entityType, entityName, date, date, evidenceType, classifier, intScore, severity);
	}

	@Override
	public void saveEvidenceInRepository(Evidence evidence) {
		saveEvidence(evidence);
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
