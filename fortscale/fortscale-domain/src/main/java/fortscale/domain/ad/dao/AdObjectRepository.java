package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.ad.AdObject;

public abstract class AdObjectRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public Long getLatestTimeStampepoch() {
		Query query = new Query();
		query.fields().include(AdObject.timestampepochField);
		query.with(new PageRequest(0, 1, Direction.DESC, AdObject.timestampepochField));
		AdObject adObject = mongoTemplate.findOne(query, AdObject.class, getCollectionName());

		// In case no fetch ever run on the system
		if (adObject == null){
			return  0l;
		}

		return adObject.getTimestampepoch();
	}

	public long countByTimestampepoch(Long timestampepoch) {
		return mongoTemplate.count(query(where(AdObject.timestampepochField).is(timestampepoch)), getCollectionName());
	}

	public abstract String getCollectionName();
}
