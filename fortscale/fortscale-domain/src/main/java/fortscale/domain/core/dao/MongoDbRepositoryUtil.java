package fortscale.domain.core.dao;

import java.util.Map;

import org.springframework.data.mongodb.MongoDbFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

public class MongoDbRepositoryUtil {
	
	private MongoDbFactory mongoDbFactory;
	
	public MongoDbFactory getMongoDbFactory() {
		return mongoDbFactory;
	}

	public void setMongoDbFactory(MongoDbFactory mongoDbFactory) {
		this.mongoDbFactory = mongoDbFactory;
	}
	
	
	public void saveMap(String collectionName, Map<String, String> attrVals) {
		DBCollection collection = getDBCollection(collectionName);

		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.putAll(attrVals);
		
		collection.insert(basicDBObject, WriteConcern.SAFE);
	}

	private DBCollection getDBCollection(String collectionName){
		DB db = mongoDbFactory.getDb();
		return db.getCollection(collectionName);
	}
	
}
