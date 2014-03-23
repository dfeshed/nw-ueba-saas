package fortscale.domain.eventscache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CachedRecordRepositoryImpl implements CachedRecordRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void deleteByCacheName(String cacheName) {
		mongoTemplate.remove(Query.query(Criteria.where("cacheName").is(cacheName)), CachedRecord.class);		
	}

}
