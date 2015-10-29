package fortscale.domain.core;

/**
 * Created by Amir Keren on 03/09/15.
 */
public class NotificationSupportingInformationFactory {

    private final static String VPN_OVERLAPPING = "VPN_user_creds_share";
    private final static String VPN_GEO_HOPPING = "vpn_geo_hopping";

    public static NotificationSupportingInformation getNotificationSupportingInformation(Evidence evidence,
                                                                                         boolean isBDPRunning) {

        switch (evidence.getAnomalyTypeFieldName()){

        case VPN_OVERLAPPING: return new VpnOverlappingSupportingInformation();

        case VPN_GEO_HOPPING: return new VpnGeoHoppingSupportingInformation(isBDPRunning);

        default: break;

        }
        return null;
    }

}