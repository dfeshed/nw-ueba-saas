package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @author gils
 * 23/05/2016
 */
public class OrganizationActivityLocations extends AbstractAuditableDocument{
    public static final String COLLECTION_NAME = "user_activity_locations";

    public static final String START_TIME_FIELD_NAME = "start_time";
    public static final String END_TIME_FIELD_NAME = "end_time";
    public static final String DATA_SOURCES_FIELD_NAME = "data_sources";
    public static final String LOCATIONS_FIELD_NAME = "locations";

    @Field(START_TIME_FIELD_NAME)
    Date startTime;

    @Field(END_TIME_FIELD_NAME)
    Date endTime;

    @Field(DATA_SOURCES_FIELD_NAME)
    private List<String> dataSources;
}

