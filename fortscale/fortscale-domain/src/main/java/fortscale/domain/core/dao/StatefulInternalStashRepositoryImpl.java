package fortscale.domain.core.dao;

import fortscale.domain.core.StatefulInternalStash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * Created by Amir Keren on 26/07/2015.
 */
public class StatefulInternalStashRepositoryImpl implements StatefulInternalStashRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override public StatefulInternalStash findBySuuid(String suuid) {
		Query query = new Query();
		query.addCriteria(Criteria.where(StatefulInternalStash.SUUID_FIELD).is(suuid));
		return mongoTemplate.findOne(query, StatefulInternalStash.class);
	}

	@Override public void updateLatestTS(String suuid, long latest_ts) {
		mongoTemplate.updateFirst(query(where(StatefulInternalStash.SUUID_FIELD).is(suuid)),
				update(StatefulInternalStash.LATEST_TS_FIELD, latest_ts), StatefulInternalStash.class);
	}

}