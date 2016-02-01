package fortscale.domain.core.dao;

import com.mongodb.DBCollection;
import fortscale.domain.core.Alert;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public long countWithParameters(long fromTime, long toTime){
		Query query = new Query(where(Evidence.startDateField).gte(fromTime).lte(toTime));
		Long count = mongoTemplate.count(query, Evidence.class);
		return count;
	}

	@Override
	public List getDistinctByFieldName(String fieldName) {
		return mongoTemplate.getCollection("evidences").distinct(fieldName);
	}

	@Override
	public List<String> getEvidenceIdsByAnomalyTypeFiledNames(String[] indicatorTypes) {

		Query query = new Query();
		query.fields().include(Evidence.ID_FIELD);
		query.addCriteria(where(Evidence.anomalyTypeFieldNameField).in(indicatorTypes));
		List<Evidence> indicators = mongoTemplate.find(query, Evidence.class);
		List<String> ids = new ArrayList<>();

		indicators.forEach(indicator -> ids.add(indicator.getId()));

		return ids;
	};



}
