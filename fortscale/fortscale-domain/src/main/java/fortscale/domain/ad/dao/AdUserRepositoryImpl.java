package fortscale.domain.ad.dao;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdUser;





class AdUserRepositoryImpl implements AdUserRepositoryCustom{

	@Autowired
	private MongoDbFactory mongoDbFactory;
	@Autowired
	private MongoTemplate mongoTemplate;

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

	@Override
	public String getLatestTimeStamp() {
		Aggregation agg = newAggregation(project(AdObject.timestampField),
				group(AdObject.timestampField),
				sort(DESC,"_id"),
				limit(1));
	
		AggregationResults<AdUserTimeStamp> result = mongoTemplate.aggregate(agg, AdUser.COLLECTION_NAME, AdUserTimeStamp.class);
		if(result.getMappedResults().isEmpty()) {
			return null;
		}
		AdUserTimeStamp ret = result.getMappedResults().get(0);
		return ret.id;
	}
	
	class AdUserTimeStamp{
		String id;
//		String timestamp;
	}
}
