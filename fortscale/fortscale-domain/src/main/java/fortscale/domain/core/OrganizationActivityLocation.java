package fortscale.domain.core;

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
@Document(collection = OrganizationActivityLocation.COLLECTION_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationActivityLocation extends AbstractAuditableDocument{
    public static final String COLLECTION_NAME = "organization_activity_locations";

    public static final String START_TIME_FIELD_NAME = "startTime";
    public static final String END_TIME_FIELD_NAME = "endTime";
    public static final String DATA_SOURCES_FIELD_NAME = "dataSources";
    public static final String LOCATIONS_FIELD_NAME = "locations";
    public static final String COUNTRY_HISTOGRAM_FIELD_NAME = "countryHistogram";

    @Indexed
    @Field(START_TIME_FIELD_NAME)
    Long startTime;

    @Field(END_TIME_FIELD_NAME)
    Long endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;

    @Field(LOCATIONS_FIELD_NAME)
    private OrganizationActivityLocation.Locations locations;

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

