package fortscale.domain.core.dao;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;

import java.util.Date;
import java.util.List;

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

	long deleteEvidenceBetween(Date startDate, Date endDate);

}
