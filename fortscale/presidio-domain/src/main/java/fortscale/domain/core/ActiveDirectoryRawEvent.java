package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document
public class ActiveDirectoryRawEvent extends AbstractPresidioDocument {

    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";
    public static final String OBJECT_ID_FIELD_NAME = "objectId";

    @Field(IS_USER_ADMIN_FIELD_NAME)
    private boolean isUserAdmin;
    @NotEmpty
    @Field(OBJECT_ID_FIELD_NAME)
    private String objectId;

    public ActiveDirectoryRawEvent(String[] event) {
        this.dateTime = Instant.parse(event[0]);
        this.eventId = event[1];
        this.dataSource = event[2];
        this.userId = event[3];
        this.operationType = event[4];
        this.result = EventResult.valueOf(event[5]);
        this.isUserAdmin = Boolean.valueOf(event[6]);
        this.objectId = event[7];
        this.operationTypeCategory = new ArrayList<>();
        for (int i = 8; i <= event.length; i++) {
            this.operationTypeCategory.add(event[i]);
        }
    }

    public ActiveDirectoryRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                   List<String> operationTypeCategory, EventResult result, String userName,
                                   String userDisplayName, Map<String, String> additionalInfo, boolean isUserAdmin,
                                   String objectId) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo);
        this.isUserAdmin = isUserAdmin;
        this.objectId = objectId;
    }

    public ActiveDirectoryRawEvent() {
    }

    @JsonProperty(value = "isUserAdmin")
    public boolean getIsUserAdministrator() {
        return isUserAdmin;
    }

    public void setIsUserAdministrator(boolean userAdministrator) {
        isUserAdmin = userAdministrator;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        isUserAdmin = userAdmin;
    }


    @Override
    public String toString() {
        return "ActiveDirectoryRawEvent{" +
                "isUserAdmin=" + isUserAdmin +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}

