package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdUser;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;





class AdUserRepositoryImpl implements AdUserRepositoryCustom{

	@Autowired
	private MongoDbFactory mongoDbFactory;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbRepositoryUtil mongoDbRepositoryUtil;

//	@SuppressWarnings("unchecked")
//	@Override
//	public List<AdUser> findAdUsersAttrVals() {
//		DBCollection collection = getDBCollection();
//		
//		List<AdUser> ret = new ArrayList<AdUser>();
//		Iterator<DBObject> iter = collection.find().iterator();
//		while(iter.hasNext()){
//			DBObject cur = iter.next();
//			AdUser adUser = new AdUser();
//			adUser.setDistinguishedName((String) cur.toMap().get(AdObject.dnField));
//			adUser.setAttrVals(cur.toMap());
//			ret.add(adUser);
//		}
//		
//		return ret;
//	}
	
//	private DBCollection getDBCollection(){
//		DB db = mongoDbFactory.getDb();
//		return db.getCollection("ad_user");
//	}

	@Override
	public Long getLatestTimeStampepoch() {
		Query query = new Query();
		query.fields().include(AdObject.timestampepochField);
		AdUser adUser = mongoTemplate.findOne(query.with(new PageRequest(0, 1, Direction.DESC, AdObject.timestampepochField)), AdUser.class);
		return adUser.getTimestampepoch();
	}

	@Override
	public long countByTimestampepoch(Long timestampepoch) {
		return mongoTemplate.count(query(where(AdObject.timestampepochField).is(timestampepoch)), AdUser.class);
	}
}
