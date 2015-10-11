package fortscale.domain.core.dao;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;

import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 01/09/2015.
 */
public interface EvidencesRepositoryCustom {

	/**
	 * Finds evidences in mongo based on entity, time and type of feature
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
	 * Count all evidences by filter.
	 * Currently the filter contain the fromTime only.
	 * Might be expand when needed
	 * @param fromTime - the time which the evidence start time should be greated or equals to
	 * @param toTime the time which the evidence start time should be smaller or equals to
	 * @return
	 */
	long countWithParameters(long fromTime, long toTime);
}
