package fortscale.domain.core.dao;

    import fortscale.domain.core.GeoHopping.CountryCity;

        
   /**
  * Created by shays on 01/09/2015.
  */
    public interface GeoHoppingRepositoryCustom {

       /**
        * Count how many evidenc took place acocrding to the filter (which one or two countrycity and for specific user)
        * The second countrycity and the user is optional
        * @param indicatorStartTime
        * @param location1
        * @param location2 - optional
        * @param username  the normalized user name of the user (optional)
        * @return
        */
       int getGeoHoppingCount(long indicatorStartTime, CountryCity location1, CountryCity location2, String username);
    }