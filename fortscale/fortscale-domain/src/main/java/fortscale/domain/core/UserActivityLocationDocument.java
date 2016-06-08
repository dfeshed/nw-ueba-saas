package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author gils
 * 23/05/2016
 */

@Document(collection = UserActivityLocationDocument.COLLECTION_NAME)
@CompoundIndexes({
        @CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityLocationDocument extends UserActivityDocument {

    public static final String COLLECTION_NAME = "user_activity_locations";
    private static final String LOCATIONS_FIELD_NAME = "locations";
    private static final String COUNTRY_HISTOGRAM_FIELD_NAME = "countryHistogram";


    @Field(LOCATIONS_FIELD_NAME)
    private Locations locations = new Locations();


    public Locations getLocations() {
        return locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    @Override
    public Map<String, Integer> getHistogram() {
        return getLocations().getCountryHistogram();
    }

    private static class Locations {
        private Map<String, Integer> countryHistogram = new HashMap<>();

        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Integer> getCountryHistogram() {
            return countryHistogram;
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserActivityLocationDocument that = (UserActivityLocationDocument) o;

        if (!normalizedUsername.equals(that.normalizedUsername)) return false;
        if (!startTime.equals(that.startTime)) return false;
        return endTime.equals(that.endTime);

    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizedUsername, startTime, endTime);
    }


}
