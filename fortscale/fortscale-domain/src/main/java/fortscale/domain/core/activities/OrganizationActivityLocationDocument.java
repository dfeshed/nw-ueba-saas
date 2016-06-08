package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Indexed
    @Field(START_TIME_FIELD_NAME)
    private Long startTime;

    @Field(END_TIME_FIELD_NAME)
    private Long endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;

    @Field(LOCATIONS_FIELD_NAME)
    private OrganizationActivityLocationDocument.Locations locations;


    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<String> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<String> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public Map<String, Integer> getHistogram() {
        return getLocations().getCountryHistogram();
    }


    public Locations getLocations() {
        return locations;
    }
    

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public static class Locations {
        private Map<String, Integer> countryHistogram = new HashMap<>();


        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Integer> getCountryHistogram() {
            return countryHistogram;
        }
    }
}

