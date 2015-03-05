package fortscale.domain.streaming.user.dao;

import fortscale.domain.streaming.user.UserScoreSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserScoreSnapshotImpl implements UserScoreSnapshotCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void deleteAllSnapshotsForClassifier(String classifierId) {
		Query query = new Query(Criteria.where(UserScoreSnapshot.CLASSIFIER_ID_FIELD).is(classifierId));
		mongoTemplate.remove(query, UserScoreSnapshot.class);
	}
}
