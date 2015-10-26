package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

/**
 * supporting information for notification evidences
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT,property="type")
@JsonSubTypes({@JsonSubTypes.Type(value = VpnGeoHoppingSupportingInformation.class,name="vpnGeoHoppingSupportingInformation"),
        @JsonSubTypes.Type(value = VpnOverlappingSupportingInformation.class,name="vpnOverlappingSupportingInformation")})
public class NotificationSupportingInformation extends EntitySupportingInformation {

    public NotificationSupportingInformation(){}

    public void setData(Evidence evidence, String json){
        throw new AbstractMethodError("Notification Supporting information is abstract!!");
    }

}