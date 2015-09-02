package fortscale.domain.core.dao;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;

import java.util.List;

/**
 * Created by tomerd on 01/09/2015.
 */
public interface EvidencesRepositoryCustom {
	List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, long startDate, long endDate,
			String dataEntities, String featureName);
}
