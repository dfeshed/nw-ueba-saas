package fortscale.services.impl;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;

import java.util.Date;

/**
 * Date: 6/23/2015.
 */
public interface EvidencesService {

	/**
	 * Create new object (transient) of evidence
	 * @param entityType    The type of the entity
	 * @param entityName    The name of the entity
	 * @param date            The date of the evidence (single date for single event)
	 * @param scoreFieldName    The field name of the anomaly
	 * @param classifier        The relevant data source
	 * @param score                The score
	 * @param anomalyValue	Value of the field
	 * @param anomalyType	Type of the anomaly
	 * @return	New evidence
	 */
	public Evidence createTransientEvidence(EntityType entityType, String entityName, Date date,
			String scoreFieldName, String classifier, Double score, String anomalyValue, String anomalyType);

	/**
	 * Create new evidence in Mongo
	 * @param evidence	The evidence
	 */
	public void saveEvidenceInRepository(Evidence evidence);
}
