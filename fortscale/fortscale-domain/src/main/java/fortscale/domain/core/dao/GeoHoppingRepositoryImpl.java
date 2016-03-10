package fortscale.domain.core.dao;
        
        import com.mongodb.BasicDBList;
        import com.mongodb.BasicDBObject;
        import com.mongodb.DBObject;
        import fortscale.domain.core.DataSourceAnomalyTypePair;
        import fortscale.domain.core.EntityType;
        import fortscale.domain.core.GeoHopping;
        import org.apache.commons.lang.StringUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.mongodb.core.MongoTemplate;
        import org.springframework.data.mongodb.core.aggregation.Aggregation;
        import org.springframework.data.mongodb.core.aggregation.AggregationResults;
        import org.springframework.data.mongodb.core.query.BasicQuery;
        import org.springframework.data.mongodb.core.query.Query;
        
        import java.util.ArrayList;
        import java.util.List;
        
        import static org.springframework.data.mongodb.core.query.Criteria.where;
        
        /**
  * Created by shays on 01/09/2015.
  */
        public class GeoHoppingRepositoryImpl implements GeoHoppingRepositoryCustom {
    
            	@Autowired
    	private MongoTemplate mongoTemplate;
    
            
            	/**
     	 * Count how many evidenc took place acocrding to the filter (which one or two countrycity and for specific user)
     	 * The second countrycity and the user is optional
     	 * @param indicatorStartTime
     	 * @param country1 
     	 * @param city1
     	 * @param country2  (optional)
     	 * @param city2  (optional)
     	 * @param username  the normalized user name of the user (optional)
     	 * @return
     	 */
            	@Override
    	public int getGeoHoppingCount(long indicatorStartTime, String country1, String city1, String country2, String city2, String username){
        		DBObject queryCondition  = new BasicDBObject();
        
                		BasicDBList andList = new BasicDBList();
        
                //		Set time
                BasicDBObject timeBasicDbObject = new BasicDBObject();
        		timeBasicDbObject.put(GeoHopping.startDateField, new BasicDBObject("$lt", indicatorStartTime));
        		andList.add(timeBasicDbObject);
        
                		//Set username
                        		if (StringUtils.isNotBlank(username)){
            			andList.add(new BasicDBObject(GeoHopping.normalizedUserNameField, username));
            		}
        
                //Set country and city
                andList.add(getCountryAndCityCondition(country1, city1));
        		if (StringUtils.isNotBlank(country2) && StringUtils.isNotBlank(city2)){
            			andList.add(getCountryAndCityCondition(country2, city2));
            		}
        
                		queryCondition .put("$and", andList);
        		// Create the query
                		Query query = new BasicQuery(queryCondition );
        		query.fields().include(GeoHopping.ID_FIELD);
        
                		//Count number of evidence of type geo_hopping by query
                        		int count = (int)mongoTemplate.count(query, GeoHopping.class);
        		return count;
        	}
    
            	/**
     	 *	Build condition for country and city
     	 * @param country
     	 * @param city
     	 * @return
     	 */
            	private DBObject getCountryAndCityCondition(String country, String city){
        
                		DBObject queryCondition = new BasicDBObject();
        		BasicDBList andList = new BasicDBList();
        
                andList.add(new BasicDBObject("locations.country", country));
        		andList.add(new BasicDBObject("locations.city", city));
        
                		queryCondition .put("$and", andList);
        		return queryCondition;
        	}
    }