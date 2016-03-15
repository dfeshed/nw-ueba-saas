package fortscale.domain.core.dao;
        
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fortscale.domain.core.GeoHopping;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.Set;

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


		//Create the list of locations (country/city)
		//location1 is mandatory, location2 is optional
		GeoHopping.CountryCity location1 = new GeoHopping.CountryCity();
		location1.setCity(city1);
		location1.setCountry(country1);
		Set<GeoHopping.CountryCity> locations = new HashSet<>();
		locations.add(location1);

		if (StringUtils.isNotBlank(country2) && StringUtils.isNotBlank(city2)){
			GeoHopping.CountryCity location2 = new GeoHopping.CountryCity();
			location2.setCity(city1);
			location2.setCountry(country1);
			locations.add(location2);
		}


		//Create the condition. Use is optional
		Criteria condition = where(GeoHopping.normalizedUserNameField).is(username).and(
				GeoHopping.startDateField).lt(indicatorStartTime).
				and(GeoHopping.locationsField).in(locations);

		if (StringUtils.isNotEmpty(username)){
			condition.and(GeoHopping.normalizedUserNameField).is(username);
		}

		Query query = new Query(condition);


		//Count number of evidence of type geo_hopping by query
		int count = (int)mongoTemplate.count(query, GeoHopping.class);
		return count;
	}
	
}