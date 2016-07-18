package fortscale.streaming.task;

import fortscale.domain.core.*;

/**
 * Created by Amir Keren on 02/09/15.
 */
public class NotificationSupportingInformationPopulator implements EntitySupportingInformationPopulator {

    @Override
    public EntitySupportingInformation populate(Evidence evidence, String data, boolean isBDPRunning) {
        NotificationSupportingInformation notificationSupportingInformation = NotificationSupportingInformationFactory.
                getNotificationSupportingInformation(evidence);
        if (notificationSupportingInformation != null) {
            notificationSupportingInformation.setData(data, isBDPRunning);
            return notificationSupportingInformation;
        }
        return null;
    }

}