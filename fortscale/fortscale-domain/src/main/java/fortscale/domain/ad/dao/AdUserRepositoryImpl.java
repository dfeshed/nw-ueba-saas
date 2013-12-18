package fortscale.domain.ad.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<AdUser> findAdUsersAttrVals() {
		DBCollection collection = getDBCollection();
		
		List<AdUser> ret = new ArrayList<AdUser>();
		Iterator<DBObject> iter = collection.find().iterator();
		while(iter.hasNext()){
			DBObject cur = iter.next();
			AdUser adUser = new AdUser();
			adUser.setDistinguishedName((String) cur.toMap().get(AdObject.dnField));
			adUser.setAttrVals(cur.toMap());
			ret.add(adUser);
		}
		
		return ret;
	}
	
	private DBCollection getDBCollection(){
		DB db = mongoDbFactory.getDb();
		return db.getCollection("ad_user");
	}

	@Override
	public String getLatestTimeStamp() {
		return mongoDbRepositoryUtil.getLatestTimeStampString(AdObject.timestampField, AdUser.COLLECTION_NAME);
	}
}
