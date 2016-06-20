package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

/**
 * @author gils
 * 23/05/2016
 */
@Document(collection = OrganizationActivityLocationDocument.COLLECTION_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationActivityLocationDocument extends UserActivityDocument {
    public static final String COLLECTION_NAME = "organization_activity_locations";
    public static final String LOCATIONS_FIELD_NAME = "locations";
    public static final String COUNTRY_HISTOGRAM_FIELD_NAME = "countryHistogram";

    private static final String RESERVED_RANGE_COUNTRY_VALUE = "Reserved Range";
    private static final String NOT_AVAILABLE_COUNTRY_VALUE = "N/A";

    private static final Set<String> countryValuesToFilter;

    static {
        countryValuesToFilter = new HashSet<>();
        countryValuesToFilter.add(RESERVED_RANGE_COUNTRY_VALUE);
        countryValuesToFilter.add(NOT_AVAILABLE_COUNTRY_VALUE);
    }



    @Field(LOCATIONS_FIELD_NAME)
    private OrganizationActivityLocationDocument.Locations locations;

    @Override
    public Map<String, Double> getHistogram() {
        return getLocations().getCountryHistogram();
    }


    private Locations getLocations() {
        return locations;
    }
    

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public static class Locations {
        private Map<String, Double> countryHistogram = new HashMap<>();


        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Double> getCountryHistogram() {
            return countryHistogram;
        }
    }


}

