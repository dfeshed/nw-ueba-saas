package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
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
public class UserActivityLocation extends AbstractAuditableDocument{
    public static final String COLLECTION_NAME = "user_activity_locations";

    private static final String USER_NAME_FIELD_NAME = "normalizedUsername";
    private static final String START_TIME_FIELD_NAME = "startTime";
    private static final String END_TIME_FIELD_NAME = "endTime";
    private static final String DATA_SOURCES_FIELD_NAME = "dataSources";
    private static final String LOCATIONS_FIELD_NAME = "locations";
    private static final String COUNTRY_HISTOGRAM_FIELD_NAME = "countryHistogram";

    @Indexed
    @Field(USER_NAME_FIELD_NAME)
    private String normalizedUsername;

    @Indexed
    @Field(START_TIME_FIELD_NAME)
    Date startTime;

    @Field(END_TIME_FIELD_NAME)
    Date endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;

    @Field(LOCATIONS_FIELD_NAME)
    private Locations locations;

    private class Locations {
        private Map<String, Integer> countryHistogram;

        @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
        public Map<String, Integer> getCountryHistogram() {
            return countryHistogram;
        }
    }
}
