package fortscale.domain.core.dao;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.core.Computer;

public class ComputerRepositoryImpl implements ComputerRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Date getLatestWhenChanged() {
		Query query = new Query();
		query.fields().include(Computer.WHEN_CHANGED_FIELD);
		query.with(new Sort(Direction.DESC, Computer.WHEN_CHANGED_FIELD));
		query.limit(1);
		
		Date latest = mongoTemplate.findOne(query, Date.class, Computer.COLLECTION_NAME);
		return latest;
	}
	
}
