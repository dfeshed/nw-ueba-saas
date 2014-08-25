package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import fortscale.domain.ad.AdUser;



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
		Query query = new Query();
		Criteria criteria = where(AdUser.dnField).regex(".*"+ou);
		query.addCriteria(criteria);
		return mongoTemplate.find(query, AdUser.class, AdUser.COLLECTION_NAME);
	}
	
	public List<AdUser> findByDnUsersIn(List<String> usersDn) {

		int chunksNumber = (int) Math.ceil((float) usersDn.size() / inOperatorSizeLimit);
		List<AdUser> users = new ArrayList<AdUser>();
		for (int i = 0; i < chunksNumber - 1; i++) {
			users.addAll(findByDnAdUsersInChunk((usersDn.subList(inOperatorSizeLimit * i, inOperatorSizeLimit * (i + 1)))));

		}
		users.addAll(findByDnAdUsersInChunk(usersDn.subList(inOperatorSizeLimit * (chunksNumber - 1), usersDn.size())));
		return users;
	}
	
	private List<AdUser> findByDnAdUsersInChunk(List<String> usersDn){
		Query query = new Query();
		Criteria criteria = where(AdUser.dnField).in(usersDn);
		query.addCriteria(criteria);
		return mongoTemplate.find(query, AdUser.class, AdUser.COLLECTION_NAME);
	}
}
