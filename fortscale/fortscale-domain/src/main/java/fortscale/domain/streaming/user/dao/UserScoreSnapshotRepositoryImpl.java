package fortscale.domain.streaming.user.dao;

import fortscale.domain.streaming.user.UserScoreSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserScoreSnapshotRepositoryImpl implements UserScoreSnapshotRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void clearAllClassifiersScores(String classifierId) {
		Query query = new Query(Criteria.where(UserScoreSnapshot.CLASSIFIER_ID_FIELD).is(classifierId));
		mongoTemplate.remove(query, UserScoreSnapshot.class);
	}
}
