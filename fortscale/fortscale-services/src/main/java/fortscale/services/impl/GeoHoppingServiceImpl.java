package fortscale.services.impl;

import fortscale.domain.core.GeoHopping;
import fortscale.domain.core.dao.GeoHoppingRepository;
import fortscale.services.GeoHoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
  * Created by shays on 09/03/2016.
  */
@Service("geoHoppingService")
public class GeoHoppingServiceImpl implements GeoHoppingService{

    @Autowired
    private GeoHoppingRepository geoHoppingRepository;

    @Override
    public int getGeoHoppingCount(long timestamp, String country1, String city1, String country2, String city2, String username){
            return geoHoppingRepository.getGeoHoppingCount(
                            timestamp, country1, city1, country2, city2, username);
        }

    @Override
    public GeoHopping save(GeoHopping geoHopping) {
        geoHopping = geoHoppingRepository.save(geoHopping);
        return geoHopping;
    }

}