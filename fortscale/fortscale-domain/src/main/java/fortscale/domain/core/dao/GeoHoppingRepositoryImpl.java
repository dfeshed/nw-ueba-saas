package fortscale.domain.core.dao;

import fortscale.domain.core.GeoHopping;
import fortscale.domain.core.GeoHopping.CountryCity;
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
	 * @param location1
	 * @param location2 - optional
	 * @param username  the normalized user name of the user (optional)
	 * @return
	 */
	@Override
	public int getGeoHoppingCount(long indicatorStartTime, CountryCity location1, CountryCity location2, String username){


		//Create the condition. Use is optional
		Criteria condition;
		if (location2 == null) {
			//If only one location, we look for location in array of locations
			condition = where(
					GeoHopping.startDateField).lt(indicatorStartTime).
					and(GeoHopping.locationsField).in(location1);
		} else {
			//If 2 locations provided, we look check that location1 in array of locations
			//AND that location2 in array of location, this is why we need to use and andOperator with 2 different conditions
			condition =
					where(GeoHopping.startDateField).lt(indicatorStartTime).
							andOperator(where(GeoHopping.locationsField).in(location1),
									    where(GeoHopping.locationsField).in(location2));
		}

		//Add condition for username, if exists
		if (StringUtils.isNotEmpty(username)){
			condition.and(GeoHopping.normalizedUserNameField).is(username);
		}



		Query query = new Query(condition);


		//Count number of evidence of type geo_hopping by query
		int count = (int)mongoTemplate.count(query, GeoHopping.class);
		return count;
	}

}