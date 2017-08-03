package presidio.output.domain.records.events;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
