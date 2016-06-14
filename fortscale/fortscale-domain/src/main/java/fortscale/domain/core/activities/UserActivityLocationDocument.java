package fortscale.domain.core.activities;

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
    public Map<String, Double> getHistogram() {
        return getLocations().getCountryHistogram();
    }

    private static class Locations {
        private Map<String, Double> countryHistogram = new HashMap<>();

        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Double> getCountryHistogram() {
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
