package presidio.sdk.api.domain.rawevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document
public class ActiveDirectoryRawEvent extends AbstractInputDocument {

    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";
    public static final String OBJECT_ID_FIELD_NAME = "objectId";

    @Field(IS_USER_ADMIN_FIELD_NAME)
    private boolean isUserAdmin;

    @Field(OBJECT_ID_FIELD_NAME)
    private String objectId;

    public ActiveDirectoryRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                   List<String> operationTypeCategory, EventResult result, String userName,
                                   String userDisplayName, Map<String, String> additionalInfo, boolean isUserAdmin,
                                   String objectId, String resultCode) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo, resultCode);
        this.isUserAdmin = isUserAdmin;
        this.objectId = objectId;
    }

    public ActiveDirectoryRawEvent() {
    }

    public ActiveDirectoryRawEvent(ActiveDirectoryRawEvent other) {
        super(other);
        this.isUserAdmin = other.isUserAdmin;
        this.objectId = other.objectId;
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
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategory=" + operationTypeCategory +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}

