package fortscale.domain.fe.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

import fortscale.domain.fe.ADFeature;
import fortscale.domain.fe.AdUserFeaturesExtraction;

public class AdUsersFeaturesExtractionRepositoryImpl implements
		AdUsersFeaturesExtractionRepositoryCustom {

	@Autowired
	private MongoDbFactory mongoDbFactory;
	
	
	@Override
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		DBCollection collection = getDBCollection();

		BasicDBObject basicDBObject = new BasicDBObject(AdUserFeaturesExtraction.userIdField, adUsersFeaturesExtraction.getUserId());
		basicDBObject.append(AdUserFeaturesExtraction.scoreField, adUsersFeaturesExtraction.getScore());
		basicDBObject.append(AdUserFeaturesExtraction.timestampField, adUsersFeaturesExtraction.getTimestamp());
		for(ADFeature adFeature: adUsersFeaturesExtraction.getAttrVals()){
			BasicDBObject featureObject = new BasicDBObject();
			featureObject.append(ADFeature.DISPLAY_NAME_FIELD, adFeature.getDisplayName());
			featureObject.append(ADFeature.FEATURE_SCORE_FIELD, adFeature.getFeatureScore());
			featureObject.append(ADFeature.FEATURE_VALUE_FIELD, adFeature.getFeatureValue());
			basicDBObject.append(adFeature.getUniqueName(), featureObject);
		}
		
		collection.insert(basicDBObject, WriteConcern.SAFE);
	}

	private DBCollection getDBCollection(){
		DB db = mongoDbFactory.getDb();
		return db.getCollection(AdUserFeaturesExtraction.collectionName);
	}
}
