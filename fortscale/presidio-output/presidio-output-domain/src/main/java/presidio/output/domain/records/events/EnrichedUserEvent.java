package presidio.output.domain.records.events;

import fortscale.utils.mongodb.index.DynamicIndexing;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
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
public class EnrichedUserEvent extends EnrichedEvent{


    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String IS_USER_ADMIN = "isUserAdmin";

    @Indexed
    @Field(USER_ID_FIELD_NAME)
    private String userId;

    @Field(USER_NAME_FIELD_NAME)
    private String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    public EnrichedUserEvent() {
    }

    public EnrichedUserEvent(Instant createdDate,
                             Instant eventDate,
                             String eventId,
                             String schema,
                             String userId,
                             String userName,
                             String userDisplayName,
                             String dataSource,
                             Map<String, String> additionalInfo) {
        super(createdDate, eventDate, eventId, schema, dataSource, additionalInfo);
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
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
}
