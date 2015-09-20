package fortscale.domain.core.dao;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by tomerd on 01/09/2015.
 */
public class EvidencesRepositoryImpl implements EvidencesRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, long startDate, long endDate,
			String dataEntities, String featureName) {
		Query query = new Query(where
				(Evidence.entityTypeField).is(entityType).and
				(Evidence.entityNameField).is(entityName).and
				(Evidence.startDateField).gte(startDate).and
				(Evidence.endDateField).lte(endDate).and
				(Evidence.dataEntityIdField).is(dataEntities).and
				(Evidence.anomalyTypeFieldNameField).is(featureName)
		);

		return mongoTemplate.find(query, Evidence.class);
	}

	@Override
	public long deleteEvidenceBetween(Date startDate, Date endDate) {
		Query query = new Query(where(Evidence.createdDateField).gte(startDate).lt(endDate));
		long numberOfEvidenceToRemove = mongoTemplate.count(query, Evidence.class);
		mongoTemplate.remove(query, Evidence.class, Evidence.COLLECTION_NAME);
		return numberOfEvidenceToRemove;
	}
}
