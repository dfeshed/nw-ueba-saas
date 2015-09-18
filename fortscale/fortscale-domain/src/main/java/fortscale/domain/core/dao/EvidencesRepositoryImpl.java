package fortscale.domain.core.dao;

import com.mongodb.WriteResult;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Calendar;
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
	public long deleteEvidenceAfterTime(Date timeAfterWhichToDelete) {
		/*Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeAfterWhichToDelete);
		calendar.add(Calendar.SECOND, Evidence.ttl);
		long targetTime = calendar.getTimeInMillis();
		//1st formula is : retention date = insertion date + ttl
		//2nd formula is : target time = timeAfterWhichToDelete + ttl
		//condition is   : retention date > target time, means all records inserted after timeAfterWhichToDelete
		Query query = new Query(where(Evidence.retentionDateField).gt(targetTime));*/
		Query query = new Query(where(Evidence.createdDateField).gt(timeAfterWhichToDelete));
		long numberOfEvidenceToRemove = mongoTemplate.count(query, Evidence.class);
		mongoTemplate.remove(query, Evidence.class, Evidence.COLLECTION_NAME);
		return numberOfEvidenceToRemove;
	}
}
