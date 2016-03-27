package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by galiar on 20/08/2015.
 */
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT,property="type")
@JsonSubTypes({@JsonSubTypes.Type(value = UserSupportingInformation.class,name="userSupportingInformation"),
        @JsonSubTypes.Type(value = NotificationSupportingInformation.class,name="notificationSupportingInformation")})
public class EntitySupportingInformation {

    public EntitySupportingInformation() {}

	public List<Map<String, Object>> generateResult(){return new ArrayList<>();}


}
