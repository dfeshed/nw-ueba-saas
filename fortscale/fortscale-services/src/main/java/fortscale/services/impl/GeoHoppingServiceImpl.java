package fortscale.services.impl;

import fortscale.domain.core.GeoHopping;
import fortscale.domain.core.dao.GeoHoppingRepository;
import fortscale.services.GeoHoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
  * Created by shays on 09/03/2016.
  */
@Service("geoHoppingService")
public class GeoHoppingServiceImpl implements GeoHoppingService{

    @Autowired
    private GeoHoppingRepository geoHoppingRepository;

    @Override
    public int getGeoHoppingCount(long indicatorStartTime, GeoHopping.CountryCity location1, GeoHopping.CountryCity location2, String username){
        return geoHoppingRepository.getGeoHoppingCount(indicatorStartTime, location1, location2, username);
    }

    @Override
    public int getMinGeoHoppingCount(long timestamp, Set<GeoHopping.CountryCity> country1Set, Set<GeoHopping.CountryCity> country2Set, String username){
        int ret = Integer.MAX_VALUE;
        if(country2Set == null){
            country2Set = new HashSet<>();
            country2Set.add(null);
        }
        for(GeoHopping.CountryCity city1: country1Set){
            for(GeoHopping.CountryCity city2: country2Set){
                int tmp = getGeoHoppingCount(timestamp, city1,	city2, username);
                if(tmp < ret){
                    ret = tmp;
                }
            }
        }

        return ret;
    }

    @Override
    public GeoHopping save(GeoHopping geoHopping) {
        geoHopping = geoHoppingRepository.save(geoHopping);
        return geoHopping;
    }

}