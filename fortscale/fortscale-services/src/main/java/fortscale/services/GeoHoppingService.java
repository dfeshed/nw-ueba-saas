package fortscale.services;
        
import fortscale.domain.core.*;

import fortscale.domain.core.GeoHopping.CountryCity;
        
        
/**
  * Date: 6/23/2015.
  */
public interface GeoHoppingService {

        /**
         * Count how many evidenc took place acocrding to the filter (which one or two countrycity and for specific user)
         * The second countrycity and the user is optional
         * @param indicatorStartTime
         * @param location1
         * @param location2  (optional)
         * @param username  the normalized user name of the user (optional)
         * @return number of indicators which match to criteria
         */
        int getGeoHoppingCount(long indicatorStartTime, CountryCity location1, CountryCity location2, String username);
        GeoHopping save(GeoHopping geoHopping);

}