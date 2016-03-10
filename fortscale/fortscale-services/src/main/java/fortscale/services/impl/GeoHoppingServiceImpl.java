package fortscale.services.impl;

    import fortscale.domain.core.GeoHopping;
    import fortscale.domain.core.dao.GeoHoppingRepository;
    import fortscale.services.GeoHoppingService;
    import org.springframework.beans.factory.annotation.Autowired;

    /**
      * Created by shays on 09/03/2016.
      */
        public class GeoHoppingServiceImpl implements GeoHoppingService{
    
        @Autowired
        private GeoHoppingRepository geoHoppingRepository;
    
        @Override
        public int getGeoHoppingCount(long timestamp, String country1, String city1, String country2, String city2, String username){
                return geoHoppingRepository.getGeoHoppingCount(
                                timestamp, country1, city1, country2, city2, username);
            }

        @Override
        public GeoHopping add(GeoHopping geoHopping) {
            geoHopping = geoHoppingRepository.save(geoHopping);
            return geoHopping;
        }

    }