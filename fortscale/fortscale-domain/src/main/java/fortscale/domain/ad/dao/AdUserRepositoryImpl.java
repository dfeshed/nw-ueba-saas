package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;


import fortscale.domain.ad.AdUser;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;



class AdUserRepositoryImpl extends AdObjectRepositoryImpl implements AdUserRepositoryCustom{
	private static final int inOperatorSizeLimit = 2000; 
	@Autowired
	private MongoTemplate mongoTemplate;
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
	
	
	public List<AdUser> findAdUsersBelongtoOU(String ou){
		Date date = MongoDbRepositoryUtil.getLatestTimeStampDate(AdUser.runTimeField, AdUser.COLLECTION_NAME);
		Query query = new Query(where(AdUser.dnField).regex(".*"+ou).andOperator(where(AdUser.runTimeField).is(date)));
		return mongoTemplate.find(query, AdUser.class, AdUser.COLLECTION_NAME);
	}
	
	public List<AdUser> findByDnUsersIn(List<String> usersDn) {
		Date date = MongoDbRepositoryUtil.getLatestTimeStampDate(AdUser.runTimeField, AdUser.COLLECTION_NAME);
		MongoDbRepositoryUtil.getLatestTimeStampDate(AdUser.runTimeField, AdUser.COLLECTION_NAME);
		int chunksNumber = (int) Math.ceil((float) usersDn.size() / inOperatorSizeLimit);
		List<AdUser> users = new ArrayList<AdUser>();
		for (int i = 0; i < chunksNumber - 1; i++) {
			users.addAll(findByDnAdUsersInChunk((usersDn.subList(inOperatorSizeLimit * i, inOperatorSizeLimit * (i + 1))), date));

		}
		users.addAll(findByDnAdUsersInChunk(usersDn.subList(inOperatorSizeLimit * (chunksNumber - 1), usersDn.size()), date));
		return users;
	}
	
	private List<AdUser> findByDnAdUsersInChunk(List<String> usersDn, Date date){
		Query query = new Query(where(AdUser.dnField).in(usersDn).andOperator(where(AdUser.runTimeField).is(date)));
		return mongoTemplate.find(query, AdUser.class, AdUser.COLLECTION_NAME);
	}
}
