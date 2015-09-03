package fortscale.streaming.task;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.NotificationSupportingInformation;
import fortscale.domain.core.VpnOverlappingSupportingInformation;

/**
 * Created by Amir Keren on 02/09/15.
 */
public class NotificationSupportingInformationPopulator implements EntitySupportingInformationPopulator {

    @Override
    public EntitySupportingInformation populate(String data) {
        NotificationSupportingInformation notificationSupportingInformation = new VpnOverlappingSupportingInformation();
        notificationSupportingInformation.setData(data);
        return notificationSupportingInformation;
    }

}