package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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

@Document(collection = UserActivityLocation.COLLECTION_NAME)
@CompoundIndexes({
        @CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityLocation {
    public static final String COLLECTION_NAME = "user_activity_locations";

    public static final String USER_NAME_FIELD_NAME = "normalizedUsername";
    public static final String START_TIME_FIELD_NAME = "startTime";
    public static final String END_TIME_FIELD_NAME = "endTime";
    public static final String DATA_SOURCES_FIELD_NAME = "dataSources";
    public static final String LOCATIONS_FIELD_NAME = "locations";
    public static final String COUNTRY_HISTOGRAM_FIELD_NAME = "countryHistogram";

    @Indexed
    @Field(USER_NAME_FIELD_NAME)
    private String normalizedUsername;

    @Indexed
    @Field(START_TIME_FIELD_NAME)
    Long startTime;

    @Field(END_TIME_FIELD_NAME)
    Long endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;

    @Field(LOCATIONS_FIELD_NAME)
    private Locations locations = new Locations();

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserActivityLocation that = (UserActivityLocation) o;

        if (!normalizedUsername.equals(that.normalizedUsername)) return false;
        if (!startTime.equals(that.startTime)) return false;
        return endTime.equals(that.endTime);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + normalizedUsername.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }

    public static class Locations {
        private Map<String, Integer> countryHistogram = new HashMap<>();

        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Integer> getCountryHistogram() {
            return countryHistogram;
        }
    }
}
