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
        @CompoundIndex(name = "user_start_time", def = "{'username': -1, 'start_time': -1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityLocation extends AbstractAuditableDocument{
    public static final String COLLECTION_NAME = "user_activity_locations";

    public static final String USER_NAME_FIELD_NAME = "username";
    public static final String START_TIME_FIELD_NAME = "start_time";
    public static final String END_TIME_FIELD_NAME = "end_time";
    public static final String DATA_SOURCES_FIELD_NAME = "data_sources";
    public static final String COUNTRY_HISTOGRAM_FIELD_NAME = "country_histogram";

    @Indexed
    @Field(USER_NAME_FIELD_NAME)
    private String userName;

    @Indexed
    @Field(START_TIME_FIELD_NAME)
    Date startTime;

    @Field(END_TIME_FIELD_NAME)
    Date endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;

    @Field(COUNTRY_HISTOGRAM_FIELD_NAME)
    private Map<String, Integer> countryHistogram;
}
