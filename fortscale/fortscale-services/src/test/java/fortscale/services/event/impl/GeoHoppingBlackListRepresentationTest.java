package fortscale.services.event.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import parquet.org.codehaus.jackson.map.type.TypeFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;


public class GeoHoppingBlackListRepresentationTest {


    @Test
    public void testJsonSerialization() {

        String json = "{\n" +
                "\"countryCityMap\" : { \"Israel\" : [\"Tel Aviv\",\"Haifa\"]},\n" +
                "\"country\" : [\"United States\",\"USA\"],\n" +
                "\"sourceIp\" : [\"145.23.65.23\",\"133.66.23.23\"]\n" +
                "}";


        Set<String> excpectedSourceIp = new HashSet<String>();
        Collections.addAll(excpectedSourceIp, "145.23.65.23", "133.66.23.23");
        Set<String> excpectedCountry = new HashSet<String>();
        Collections.addAll(excpectedCountry, "United States", "USA");
        HashMap<String, Set<String>> excpectedCountryCityMap = new HashMap<>();
        Set<String> cities = new HashSet<>();
        Collections.addAll(cities, "Tel Aviv", "Haifa");
        excpectedCountryCityMap.put("Israel", cities);


        VpnServiceImpl.GeoHoppingBlackListRepresentation geoHoppingBlackListRepresentation;

        ObjectMapper mapper = new ObjectMapper();

        try {
            geoHoppingBlackListRepresentation = mapper.readValue(json, VpnServiceImpl.GeoHoppingBlackListRepresentation.class);
            assertTrue(geoHoppingBlackListRepresentation.getSourceIp().equals(excpectedSourceIp) && geoHoppingBlackListRepresentation.getCountry().equals(excpectedCountry) && geoHoppingBlackListRepresentation.getCountryCityMap().equals(excpectedCountryCityMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

