package fortscale.services;
        
        import fortscale.domain.core.*;
        

        
        
        /**
  * Date: 6/23/2015.
  */
        public interface GeoHoppingService {
    
            	/**
     	 * Count how many evidenc took place acocrding to the filter (which one or two countrycity and for specific user)
     	 * The second countrycity and the user is optional
     	 * @param indicatorStartTime
     	 * @param country1 
     	 * @param city1
     	 * @param country2  (optional)
     	 * @param city2  (optional)
     	 * @param username  the normalized user name of the user (optional)
     	 * @return number of indicators which match to criteria
     	 */
        int getGeoHoppingCount(long indicatorStartTime, String country1, String city1, String country2, String city2, String username);



        GeoHopping add(GeoHopping geoHopping);

  }