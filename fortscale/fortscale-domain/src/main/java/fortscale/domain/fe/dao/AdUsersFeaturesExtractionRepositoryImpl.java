package fortscale.domain.fe.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

import fortscale.domain.fe.ADFeature;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;

public class AdUsersFeaturesExtractionRepositoryImpl implements
		AdUsersFeaturesExtractionRepositoryCustom {

	@Autowired
	private MongoDbFactory mongoDbFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		DBCollection collection = getDBCollection();

		BasicDBObject basicDBObject = new BasicDBObject(AdUserFeaturesExtraction.userIdField, adUsersFeaturesExtraction.getUserId());
		basicDBObject.append(AdUserFeaturesExtraction.scoreField, adUsersFeaturesExtraction.getScore());
		basicDBObject.append(AdUserFeaturesExtraction.timestampField, adUsersFeaturesExtraction.getTimestamp());
		for(IFeature adFeature: adUsersFeaturesExtraction.getAttrVals()){
			BasicDBObject featureObject = new BasicDBObject();
			featureObject.append(ADFeature.DISPLAY_NAME_FIELD, adFeature.getFeatureDisplayName());
			featureObject.append(ADFeature.FEATURE_SCORE_FIELD, adFeature.getFeatureScore());
			featureObject.append(ADFeature.FEATURE_VALUE_FIELD, adFeature.getFeatureValue());
			basicDBObject.append(adFeature.getFeatureUniqueName(), featureObject);
		}
		
		collection.insert(basicDBObject, WriteConcern.SAFE);
	}

	private DBCollection getDBCollection(){
		DB db = mongoDbFactory.getDb();
		return db.getCollection(AdUserFeaturesExtraction.collectionName);
	}
	
	public Double calculateAvgScore(Date timestamp){
		Aggregation agg = newAggregation(match(where(AdUserFeaturesExtraction.timestampField).is(timestamp)),
				project(AdUserFeaturesExtraction.timestampField, AdUserFeaturesExtraction.scoreField),
				group(AdUserFeaturesExtraction.timestampField).avg(AdUserFeaturesExtraction.scoreField).as("score"));
	
		AggregationResults<TimeStampAvgScore> result = mongoTemplate.aggregate(agg, AdUserFeaturesExtraction.collectionName, TimeStampAvgScore.class);
		TimeStampAvgScore ret = result.getMappedResults().get(0);
		return ret.score;
	}
	
	class TimeStampAvgScore{
		String id;
		Double score;
	}
}
