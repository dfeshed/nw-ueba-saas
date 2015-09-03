package fortscale.streaming.task;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.NotificationSupportingInformation;
import fortscale.domain.core.NotificationSupportingInformationFactory;
import fortscale.domain.core.VpnOverlappingSupportingInformation;

/**
 * Created by Amir Keren on 02/09/15.
 */
public class NotificationSupportingInformationPopulator implements EntitySupportingInformationPopulator {

    @Override
    public EntitySupportingInformation populate(String notificationType, String data) {
        NotificationSupportingInformation notificationSupportingInformation = NotificationSupportingInformationFactory.
                getNotificationSupportingInformation(notificationType);
        if (notificationSupportingInformation != null) {
            notificationSupportingInformation.setData(data);
            return notificationSupportingInformation;
        }
        return null;
    }

}