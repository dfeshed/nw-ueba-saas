package fortscale.domain.core.dao;
        import fortscale.domain.core.DataSourceAnomalyTypePair;
        import fortscale.domain.core.EntityType;
        import fortscale.domain.core.Evidence;
        
        import java.util.List;
        
   /**
  * Created by shays on 01/09/2015.
  */
    public interface GeoHoppingRepositoryCustom {

        public int getGeoHoppingCount(long timestamp, String country1, String city1, String country2, String city2, String username);
    }