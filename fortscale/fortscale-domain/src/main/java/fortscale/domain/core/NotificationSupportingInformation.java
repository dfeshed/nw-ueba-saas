package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

/**
 * supporting information for notification evidences
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public abstract class NotificationSupportingInformation extends EntitySupportingInformation {

    public abstract void setData(Evidence evidence, String json);

}