package fortscale.domain.core;

/**
 * Created by Amir Keren on 03/09/15.
 */
public class NotificationSupportingInformationFactory {

    private final static String VPN_OVERLAPPING = "VPN_user_creds_share";

    public static NotificationSupportingInformation getNotificationSupportingInformation(Evidence evidence) {
        //TODO - add additional types of notifications here
        if (evidence.getAnomalyTypeFieldName().equalsIgnoreCase(VPN_OVERLAPPING)) {
            return new VpnOverlappingSupportingInformation();
        }
        return null;
    }

}