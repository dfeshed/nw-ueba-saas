package fortscale.domain.core.dao;
        import fortscale.domain.core.DataSourceAnomalyTypePair;
        import fortscale.domain.core.EntityType;
        import fortscale.domain.core.Evidence;
        
        import java.util.List;
        
   /**
  * Created by shays on 01/09/2015.
  */
    public interface GeoHoppingRepositoryCustom {

       /**
        *  Count how many, we had geo hopping events on specific user,  with city1 & city2, before timestamp
        *
        * @param timestamp - The most recent geo hopping start time
        * @param country1 -
        * @param city1
        * @param country2 - Optional
        * @param city2 - Optional
        * @param username - normalized username. - Optional
        * @return
        */
        int getGeoHoppingCount(long timestamp, String country1, String city1, String country2, String city2, String username);
    }