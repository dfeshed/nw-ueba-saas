package fortscale.common.event;

/**
 * Created by rans on 01/03/16.
 *
 */
public enum NotificationAnomalyType {
    VPN_GEO_HOPPING("vpn_geo_hopping","country"),
    VPN_USER_CREDS_SHARE("VPN_user_creds_share", "sessions_cnt"),
    VPN_LATERAL_MOVEMENT("VPN_user_lateral_movement", "normalized_username"),
    ADMIN_GROUP_REMOVED("admin_group_removed", "admin_group"),
    HAS_NEW_ADMIN_GROUP("has_new_admin_group","admin_group"),
    USER_WAS_CREATED("user_was_created", null),
    USER_WAS_DELETED("user_was_deleted", null ),
    ABOUT_TO_EXPIRE_WEEK("about_to_expire_week", null),
    ABOUT_TO_EXPIRE_DAY("about_to_expire_day", null),
    USER_WAS_ENABLED("user_was_enabled", null),
    USER_WAS_DISABLED("user_was_disabled", null);

    NotificationAnomalyType(String type, String param) {
        this.type = type;
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public String getParam() {
        return param;
    }

    private String type;
    private String param;

}
