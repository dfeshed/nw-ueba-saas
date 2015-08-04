package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdUser;

public class AdObjectRepositoryImpl {
	@Autowired
	private MongoTemplate mongoTemplate;


	public Long getLatestTimeStampepoch() {
		Query query = new Query();
		query.fields().include(AdObject.timestampepochField);
		AdUser adUser = mongoTemplate.findOne(query.with(new PageRequest(0, 1, Direction.DESC, AdObject.timestampepochField)), AdUser.class);
		return adUser.getTimestampepoch();
	}

	public long countByTimestampepoch(Long timestampepoch) {
		return mongoTemplate.count(query(where(AdObject.timestampepochField).is(timestampepoch)), AdUser.class);
	}
}
