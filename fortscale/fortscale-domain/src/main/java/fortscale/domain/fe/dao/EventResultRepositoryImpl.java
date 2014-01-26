package fortscale.domain.fe.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.fe.EventResult;

public class EventResultRepositoryImpl implements EventResultRepositoryCustom{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<EventResult> findEventResultsBySqlQueryAndCreatedAt(String sqlQuery, DateTime createdAt, Pageable pageable) {
		Criteria criteria = where(EventResult.sqlQueryField).is(sqlQuery);
		addCreatedAt(criteria, createdAt);
		Query query = query(criteria);
		query.with(pageable);
		return mongoTemplate.find(query,EventResult.class);
	}

	@Override
	public void updateLastRetrieved(String sqlQuery, DateTime createdAt) {
		Date date = new Date();
		Criteria criteria = where(EventResult.sqlQueryField).is(sqlQuery);
		addCreatedAt(criteria, createdAt);
		Query query = query(criteria);
		mongoTemplate.updateMulti(query, update(EventResult.lastRetrievedField, date), EventResult.class);
	}

	@Override
	public List<EventResult> findEventResultsBySqlQueryAndCreatedAtAndGtMinScore(
			String sqlQuery, DateTime createdAt, Integer minScore, Pageable pageable) {
		if(minScore == null){
			return findEventResultsBySqlQueryAndCreatedAt(sqlQuery, createdAt, pageable);
		}
		Criteria criteria = where(EventResult.sqlQueryField).is(sqlQuery).and(EventResult.eventScoreField).gte(minScore);
		addCreatedAt(criteria, createdAt);
		Query query = new Query(criteria);
		query.with(pageable);
		return mongoTemplate.find(query,EventResult.class);
	}
	
	private void addCreatedAt(Criteria criteria, DateTime createdAt){
		if(createdAt != null){
			criteria.and(EventResult.CREATED_AT_FIELD_NAME).is(createdAt);
		}
	}

	public DateTime getLatestCreatedAt() {
		Query query = new Query();
		query.fields().include(EventResult.CREATED_AT_FIELD_NAME);
		EventResult eventResult = mongoTemplate.findOne(query.with(new PageRequest(0, 1, Direction.DESC, EventResult.CREATED_AT_FIELD_NAME)), EventResult.class);
		return eventResult != null ? eventResult.getCreatedAt() : null;
	}
}
