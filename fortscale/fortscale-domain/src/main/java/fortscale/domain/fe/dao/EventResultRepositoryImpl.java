package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.fe.EventResult;

public class EventResultRepositoryImpl implements EventResultRepositoryCustom{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<EventResult> findEventResultsBySqlQuery(String sqlQuery, Pageable pageable) {
		Query query = new Query(where(EventResult.sqlQueryField).is(sqlQuery));
		query.with(pageable);
		return mongoTemplate.find(query,EventResult.class);
	}

	@Override
	public void updateLastRetrieved(String sqlQuery) {
		Date date = new Date();
		mongoTemplate.updateMulti(query(where(EventResult.sqlQueryField).is(sqlQuery)), update(EventResult.lastRetrievedField, date), EventResult.class);
	}

	@Override
	public List<EventResult> findEventResultsBySqlQueryAndGtMinScore(
			String sqlQuery, Integer minScore, Pageable pageable) {
		if(minScore == null){
			return findEventResultsBySqlQuery(sqlQuery, pageable);
		}
		Query query = new Query(where(EventResult.sqlQueryField).is(sqlQuery).and(EventResult.eventScoreField).gte(minScore));
		query.with(pageable);
		return mongoTemplate.find(query,EventResult.class);
	}

}
