package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by galiar on 20/08/2015.
 */
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT,property="type",defaultImpl=Void.class)
@JsonSubTypes({@JsonSubTypes.Type(value = UserSupportingInformation.class,name="userSupportingInformation"),
        @JsonSubTypes.Type(value = NotificationSupportingInformation.class,name="notificationSupportingInformation")})
public class EntitySupportingInformation {

    public EntitySupportingInformation() {}

}
