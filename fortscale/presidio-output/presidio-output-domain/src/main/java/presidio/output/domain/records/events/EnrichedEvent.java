package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import fortscale.utils.mongodb.index.DynamicIndexing;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * A basic Output enriched event document.
 * All Output enriched event records (across all data sources) should extend this one.
 * <p>
 * Created by Efrat Noam on 02/08/2017.
 */
@Document
@DynamicIndexing(compoundIndexes = {
        @CompoundIndex(name = "userTime", def = "{'userId': 1, 'eventDate': 1}"),
})
public class EnrichedEvent {

    public static final String EVENT_ID_FIELD_NAME= "eventId";
    public static final String SCHEMA_FIELD_NAME = "schema";
    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String USERNAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userdisplayName";
    public static final String ADDITIONAL_INFO_FIELD_NAME = "additionalInfo";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String START_INSTANT_FIELD_NAME  = "eventDate";



    public static final String IS_USER_ADMIN = "isUserAdmin";

    @Id
    @Field
    private String id;

    @CreatedDate
    private Instant createdDate;

    @Field(START_INSTANT_FIELD_NAME)
    @Indexed
    private Instant eventDate;

    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    @Field(SCHEMA_FIELD_NAME)
    private String schema;

    @Indexed
    @Field(USER_ID_FIELD_NAME)
    private String userId;

    @Field(USERNAME_FIELD_NAME)
    private String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    @Field(DATA_SOURCE_FIELD_NAME)
    private String dataSource;

    @Field(ADDITIONAL_INFO_FIELD_NAME)
    private Map<String, String> additionalInfo;

    public EnrichedEvent() {
    }

    public EnrichedEvent(Instant createdDate,
                         Instant eventDate,
                         String eventId,
                         String schema,
                         String userId,
                         String userName,
                         String userDisplayName,
                         String dataSource,
                         Map<String, String> additionalInfo) {
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.eventId = eventId;
        this.schema = schema;
        this.dataSource = dataSource;
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.additionalInfo = additionalInfo;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getId() {
        return id;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }
}
