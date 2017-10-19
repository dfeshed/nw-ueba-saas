package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 02/08/2017.
 */
@Document
public class ActiveDirectoryEnrichedEvent extends EnrichedEvent{

    public static final String IS_USER_ADMIN_FIELD = "isUserAdmin";
    public static final String OBJECT_ID = "objectId";

    @Field(IS_USER_ADMIN_FIELD)
    private Boolean isUserAdmin;

    @Field(OBJECT_ID)
    private String objectId;

    public ActiveDirectoryEnrichedEvent() {
    }

    public ActiveDirectoryEnrichedEvent(Instant createdDate,
                                        Instant eventDate,
                                        String eventId,
                                        String schema,
                                        String userId,
                                        String userName,
                                        String userDisplayName,
                                        String dataSource,
                                        String operationType,
                                        List<String> operationTypeCategories,
                                        EventResult result,
                                        String resultCode,
                                        Map<String, String> additionalInfo,
                                        Boolean isUserAdmin,
                                        String objectId) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, operationType, operationTypeCategories, result, resultCode, additionalInfo);
        this.isUserAdmin = isUserAdmin;
        this.objectId = objectId;
    }

    public Boolean getUserAdmin() {
        return isUserAdmin;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setUserAdmin(Boolean userAdmin) {
        isUserAdmin = userAdmin;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
