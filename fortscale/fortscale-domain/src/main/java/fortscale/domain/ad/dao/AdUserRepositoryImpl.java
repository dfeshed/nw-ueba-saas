package fortscale.domain.ad.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdUser;





class AdUserRepositoryImpl implements AdUserRepositoryCustom{

	@Autowired
	private MongoDbFactory mongoDbFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<AdUser> findAdUsersAttrVals() {
		DBCollection collection = getDBCollection();
		
		List<AdUser> ret = new ArrayList<AdUser>();
		Iterator<DBObject> iter = collection.find().iterator();
		while(iter.hasNext()){
			DBObject cur = iter.next();
			AdUser adUser = new AdUser((String) cur.toMap().get(AdObject.dnField));
			adUser.setAttrVals(cur.toMap());
			ret.add(adUser);
		}
		
		return ret;
	}
	
	private DBCollection getDBCollection(){
		DB db = mongoDbFactory.getDb();
		return db.getCollection("ad_user");
	}
	
}
